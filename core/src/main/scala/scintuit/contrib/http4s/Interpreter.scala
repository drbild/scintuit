package scintuit
package contrib.http4s

import argonaut._
import monocle._
import org.http4s.Uri
import org.http4s.EntityDecoder
import org.http4s.Http4s._
import org.http4s.Status.ResponseClass.Successful
import org.http4s.argonaut._
import org.http4s.client.Client
import scintuit.IntuitApi.IntuitOp._
import scintuit.IntuitApi._
import scintuit.auth.{OAuthConsumer, OAuthToken}
import scintuit.contrib.argonaut._
import scintuit.contrib.argonaut.apiOptics._
import scintuit.data.{Institution, InstitutionSummary}
import scintuit.util.argonaut.codecJson._
import scintuit.util.argonaut.syntax.all._

import scalaz.concurrent.Task
import scalaz.~>

object Interpreter extends OAuthSupport {

  // ================================ Interpreter ================================
  def trans(client: Client, consumer: OAuthConsumer, token: OAuthToken) = {
    val signed = SignRequests(consumer, token)(client)

    /* Configuration*/
    val BASE = uri("https://financialdatafeed.platform.intuit.com/v1")

    /** convert a json <-> json optic into a decoder */
    def fromOptic[A](optional: Optional[Json, Json])(implicit decode: DecodeJson[A], encode: EncodeJson[A]):
    EntityDecoder[A] =
      jsonOf((optional composePrism as[A]).toDecodeJson)

    /** issue a get request and handle the response */
    def get[A](path: String)(view: Optional[Json, Json])(implicit decode: DecodeJson[A], encode: EncodeJson[A]) = {
      val uri = BASE / path
      signed(uri) flatMap {
        case Successful(res) => res.as[A](fromOptic(view))
        case res => Task.fail(IntuitAPIError(uri, res.status.code))
      }
    }

    new (IntuitOp ~> Task) {
      override def apply[A](fa: IntuitOp[A]): Task[A] =
        fa match {
          case ListInstitution => get[List[InstitutionSummary]]("institutions")(institutionsO)
          case GetInstitution(id) => get[Institution](s"institutions/${id}")(institutionO)
        }
    }
  }

  case class IntuitAPIError(uri: Uri, status: Int) extends Exception(s"Got ${status} response for ${uri}")
}
