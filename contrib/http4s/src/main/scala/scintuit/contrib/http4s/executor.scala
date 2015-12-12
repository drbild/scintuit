package scintuit.contrib.http4s

import org.http4s.EntityEncoder._
import org.http4s._
import org.http4s.client.Client
import scintuit.util.http
import scintuit.util.http.Executor
import scintuit.util.oauth.{OAuthConsumer, OAuthToken}
import scodec.bits.ByteVector

import scalaz.concurrent.Task
import scalaz.syntax.monad._

import scala.language.postfixOps

object executor {

  class Http4SExecutor(client: Client) extends Executor[Task] {

    private def execute(request: http.Request, client: Client): Task[http.Response] =
      convertRequest(request) >>= client.apply >>= convertResponse

    override def execute(request: http.Request): Task[http.Response] =
      execute(request, client)

    override def execute(request: http.Request, consumer: OAuthConsumer, token: OAuthToken): Task[http.Response] =
      execute(request, oauth.signing(consumer, token)(client))
  }

  private val stringEncoder = simple[String]()(s => ByteVector.view(s.getBytes(DefaultCharset.nioCharset)))
  private val stringDecoder = EntityDecoder.binary map (bs => new String(bs.toArray, DefaultCharset.nioCharset))

  private def convertRequest(request: http.Request): Task[Request] = {
    val method = convertMethod(request.method)
    val headers = convertHeaders(request.headers)
    val uri = Uri.fromString(request.uri).getOrElse(throw new Exception(s"Bad uri: ${request.uri}"))
    request.body match {
      case Some(body) => Request(method, uri, headers = headers).withBody(body)(stringEncoder)
      case None => Request(method, uri, headers = headers).point[Task]
    }
  }

  private def convertResponse[A](response: Response): Task[http.Response] = {
    val status = response.status.code
    val headers = convertHeaders(response.headers)
    response.as(stringDecoder) map (http.Response(status, headers, _))
  }

  private def convertMethod(method: http.Method): Method = method match {
    case http.Delete => Method.DELETE
    case http.Get => Method.GET
    case http.Post => Method.POST
    case http.Put => Method.PUT
  }

  private def convertHeaders(headers: Set[(String, String)]): Headers = Headers {
    headers map { case (k, v) => Header(k, v) } toList
  }

  private def convertHeaders(headers: Headers): Set[(String, String)] =
    headers.toList map (h => h.name.value -> h.value) toSet
}
