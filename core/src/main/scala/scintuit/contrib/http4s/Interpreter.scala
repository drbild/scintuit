package scintuit
package contrib.http4s

import org.http4s.Http4s._
import org.http4s.Status.ResponseClass.Successful
import org.http4s.client.Client
import org.http4s.{Method, Request, Uri}
import play.api.libs.json._
import scintuit.IntuitApi.IntuitOp._
import scintuit.IntuitApi._
import scintuit.auth.{OAuthConsumer, OAuthToken}
import scintuit.contrib.play.AccountFormats._
import scintuit.contrib.play.InstitutionFormats._
import scintuit.contrib.play.{ApiTransforms, PlayJsonInstances}
import scintuit.data._

import scalaz.concurrent.Task
import scalaz.~>

object Interpreter extends OAuthSupport with PlayJsonInstances with ApiTransforms {

  // ================================ Interpreter ================================
  def trans(client: Client, consumer: OAuthConsumer, token: OAuthToken) = {
    val signed = SignRequests(consumer, token)(client)

    /* Configuration*/
    val BASE = uri("https://financialdatafeed.platform.intuit.com/v1")

    /** issue a get request and handle the response */
    def get[A](path: String, transform: Reads[JsValue])(implicit reads: Reads[A]) = {
      val uri = BASE / path
      signed(uri) flatMap {
        //case Successful(res) => res.as[A](jsonOf(reads compose Reads(x => {println(Json.prettyPrint(x)); val y = transform.reads(x); println(Json.prettyPrint(y.get)); y})))
        case Successful(res) => res.as[A](jsonOf(reads compose transform))
        case res => Task.fail(IntuitAPIError(uri, res.status.code))
      }
    }

    def delete(path: String) = {
      val uri = BASE / path
      val req = Request(Method.DELETE, uri)
      signed(req) flatMap {
        case Successful(res) => Task.now(())
        case res => Task.fail(IntuitAPIError(uri, res.status.code))
      }
    }

    new (IntuitOp ~> Task) {
      override def apply[A](fa: IntuitOp[A]): Task[A] =
        fa match {
          case ListInstitutions => get[Seq[InstitutionSummary]]("institutions", listInstitutionsT)
          case GetInstitution(id) => get[Institution](s"institutions/${id}", getInstitutionT)

          case GetAccount(id) => get[Account](s"accounts/${id}", getAccountT)
          case ListCustomerAccounts => get[Seq[Account]](s"accounts", listAccountsT)
          case ListLoginAccounts(id) => get[Seq[Account]](s"logins/${id}/accounts", listAccountsT)
          case DeleteAccount(id) => delete(s"accounts/${id}")

          case _ => Task.fail(new RuntimeException("not implemented"))
        }
    }
  }

  case class IntuitAPIError(uri: Uri, status: Int) extends Exception(s"Got ${status} response for ${uri}")
}
