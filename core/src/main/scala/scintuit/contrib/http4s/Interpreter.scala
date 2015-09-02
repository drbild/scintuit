package scintuit
package contrib.http4s

import argonaut.Argonaut._
import argonaut._
import monocle.Monocle._
import monocle._
import org.http4s.Http4s._
import org.http4s.Status.ResponseClass.Successful
import org.http4s.argonaut._
import org.http4s.client.Client
import org.http4s.client.oauth1._
import org.http4s.{DecodeResult => _, _}
import scintuit.IntuitApi.IntuitOp._
import scintuit.IntuitApi._
import scintuit.auth.{OAuthConsumer, OAuthToken}
import scintuit.contrib.argonaut._
import scintuit.data.{Institution, InstitutionSummary}
import scintuit.util.argonaut.codecJson._
import scintuit.util.argonaut.syntax.all._
import scintuit.util.monocle.syntax.all._

import scalaz.concurrent.Task
import scalaz.~>


object Interpreter extends OAuthSupport {

  // ================================ JSON Helpers ================================
  def path(segments: JsonField*): Optional[Json, Option[Json]] =
    segments
      .map(jObjectPrism composeLens at(_))
      .reduceLeft(_ composePrism some composeOptional _)

  // ================================ Interpreter ================================
  def trans(client: Client)(consumer: OAuthConsumer)(token: OAuthToken) = {
    /* Configuration*/
    val BASE = uri("https://financialdatafeed.platform.intuit.com/v1")
    val signingClient = SignRequests(consumer, token)(client)

    /** convert a json <-> json optic into a decoder */
    def optic[A](optional: Optional[Json, Json])(implicit decode: DecodeJson[A], encode: EncodeJson[A]):
    DecodeJson[A] =
      (optional composePrism as[A]).toDecodeJson

    /** issue a get request and handle the response */
    def get[A](path: String)(view: Optional[Json, Json] /*= Optional.id[Json]*/)(
      implicit
      decode: DecodeJson[A], encode: EncodeJson[A]) =
      signingClient(BASE / path) flatMap {
        case Successful(res) => res.as(jsonOf(optic[A](view)))
        case res => Task.fail(IntuitAPIError(res.status.code))
      }

    /* Lens to view Intuit responses as sane Json */
    val _institutions = path("institution") composePrism some
    val _institution = (path("keys") composePrism some) mapOptional (path("key") composePrism some)

    new (IntuitOp ~> Task) {
      override def apply[A](fa: IntuitOp[A]): Task[A] =
        fa match {
          case ListInstitution => get[List[InstitutionSummary]]("institutions")(_institutions)
          case GetInstitution(id) => get[Institution](s"institutions/${id}")(_institution)
        }
    }
  }

  case class IntuitAPIError(status: Int) extends Exception(s"Bad Response: ${status}")

  private object SignRequests extends OAuthSupport {
    def apply(consumer: OAuthConsumer, token: OAuthToken)(client: Client): Client = new Client {

      override def shutdown(): Task[Unit] = client.shutdown

      override def prepare(req: Request): Task[Response] = {
        val r = signRequest(req, consumer, callback = None, verifier = None, token = Some(token))
          .map(_.putHeaders(Header("Accept", "application/json")))
        client.prepare(r)
      }
    }
  }
}
