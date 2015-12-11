/*
 * Copyright 2015 David R. Bild
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scintuit
package contrib.http4s

import org.http4s.client.Client
import org.http4s.headers.`Content-Type`
import org.http4s.{DateTime => _, _}
import scintuit.util.cache.ExpiringLruCache
import scintuit.util.config.IntuitConfig
import scintuit.util.interpreter.{Context, Stage, Interpreter}
import scintuit.util.oauth.OAuthToken
import scintuit.util.request
import scintuit.util.request.{IntuitRequest, RequestEncoder}
import scintuit.util.response.{IntuitResponse, ResponseDecoder}
import scodec.bits.ByteVector

import scala.concurrent.{ExecutionContext, Promise, Future}
import scala.concurrent.duration._
import scala.util.{Success, Failure}
import scalaz.{\/-, -\/}
import scalaz.concurrent.Task
import scalaz.syntax.either._
import scalaz.syntax.monad._

import scala.language.postfixOps

class Http4sInterpreter(
  client: Client,
  config: IntuitConfig
)(
  implicit
  encoder: RequestEncoder,
  decoder: ResponseDecoder,
  ec: ExecutionContext
) extends Interpreter[Task] with Translation with EntityEncoderInstances with EntityDecoderInstances {

  private val CACHE_SIZE = 10000
  private val CACHE_EXPIRY_PERIOD = 10 minutes

  private val tokenCache = ExpiringLruCache[String, OAuthToken](CACHE_SIZE, CACHE_EXPIRY_PERIOD)

  protected def execute[A, C: Customer](request: IntuitRequest): Stage[Task, A, C, IntuitResponse] = Stage {
    case Context(_, customer) =>
      for {
        req     <- toRequest(request)
        token   <- findToken(customer)
        prepFor =  PrepFor.prepFor(jsonStringDecoder)(client)
        signing =  OAuth.signing(config.oauthConsumer, token)(prepFor)
        res     <- signing(req) flatMap (toResponse(_))
      } yield res
  }

  private def toRequest(request: IntuitRequest): Task[Request] = {
    val method = methodToHttp4s(request.method).point[Task]
    val url = Uri.fromString(request.uri).fold(f => Task.fail(new Exception(s"Parse failure: ${f.msg}")), Task.now)
    val headers = headersToHttp4s(request.headers).point[Task]
    val req = (method |@| url |@| headers)((m, u, h) => Request(m, u, headers = h))
    request.body match {
      case Some(body) =>
        req flatMap {_.withBody(body)(jsonStringEncoder)}
      case None => req
    }
  }

  private def toResponse[A](response: Response): Task[IntuitResponse] = {
    val status = response.status.code
    val headers = headersToScintuit(response.headers)
    response.as(jsonStringDecoder) map (IntuitResponse(status, headers, _))
  }

  private def findToken[C: Customer](customer: C): Task[OAuthToken] = toTask {
    tokenCache.get(Customer[C].name(customer), c => fetchToken(c))
  }

  private def fetchToken[C: Customer](customer: C): Future[OAuthToken] = toFuture {
    OAuth.tokenForCustomer(client, config, customer)
  }

  private def toTask[A](future: Future[A]): Task[A] =
    Task.async { register =>
      future.onComplete {
        case Success(a) => register(a.right)
        case Failure(e) => register(e.left)
      }
    }

  private def toFuture[A](task: Task[A]): Future[A] = {
    val p: Promise[A] = Promise()
    task.runAsync {
      case -\/(e) => p.failure(e)
      case \/-(a) => p.success(a)
    }
    p.future
  }

}

trait Translation {
  def methodToHttp4s(method: request.Method): Method = method match {
    case request.Delete => Method.DELETE
    case request.Get => Method.GET
    case request.Post => Method.POST
    case request.Put => Method.PUT
  }

  def headersToHttp4s(headers: Map[String, String]): Headers = Headers {
    headers map { case (k, v) => Header(k, v) } toList
  }

  def headersToScintuit(headers: Headers): Map[String, String] =
    headers.toList.map(h => (h.name.value, h.value)).toMap
}

trait EntityEncoderInstances {
  import org.http4s.EntityEncoder._

  implicit val jsonStringEncoder: EntityEncoder[String] = {
    val hdr = `Content-Type`(MediaType.`application/json`).withCharset(DefaultCharset)
    simple(hdr)(s => ByteVector.view(s.getBytes(DefaultCharset.nioCharset)))
  }
}

trait EntityDecoderInstances {
  import org.http4s.EntityDecoder._

  implicit val jsonStringDecoder: EntityDecoder[String] =
    EntityDecoder.decodeBy(MediaType.`application/json`)(msg =>
      collectBinary(msg).map(bs => new String(bs.toArray, msg.charset.getOrElse(DefaultCharset).nioCharset))
    )
}
