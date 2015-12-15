package scintuit.util

import scintuit.data.raw.account._
import scintuit.data.raw.error._
import scintuit.data.raw.institution._
import scintuit.data.raw.login._
import scintuit.data.raw.position._
import scintuit.data.raw.transaction._
import scintuit.raw.intuit.IntuitOp
import scintuit.raw.intuit.IntuitOp._
import scintuit.util.http.Response
import scintuit.customer.Customer
import scintuit.exception._

import scalaz.syntax.apply._
import scalaz.syntax.either._
import scalaz.syntax.std.option._
import scalaz.{-\/, Catchable, Monad, \/, \/-}

object parse {

  trait Decoder {
    def account(body: String): Exception \/ Account
    def accounts(body: String): Exception \/ Vector[Account]
    def challenges(body: String): Exception \/ Vector[Challenge]
    def errorInfo(body: String): Exception \/ ErrorInfo
    def institution(body: String): Exception \/ InstitutionDetails
    def institutions(body: String): Exception \/ Vector[Institution]
    def positions(body: String): Exception \/ Vector[Position]
    def transactions(body: String): Exception \/ TransactionsResponse

    def challengeSession(headers: Map[String, String], body: String): Exception \/ ChallengeSession = {
      val sessionId = headers.get("challengeSessionId") \/> new Exception("Missing header: challengeSessionId")
      val nodeId = headers.get("challengeNodeId") \/> new Exception("Missing header: challengeNodeId")
      val challenges = this.challenges(body)
      (sessionId |@| nodeId |@| challenges)(ChallengeSession(_, _, _))
    }

  }

  type PartialDecode[A] = PartialFunction[Response, Exception \/ A]

  private def loginError(decode: Decoder)(body: String): Option[LoginError] =
    decode.errorInfo(body).toOption flatMap (_.errorCode) flatMap LoginError.errorCode

  def decodeErrorInfo[C: Customer](decode: Decoder)(customer: C, op: IntuitOp[_]): PartialDecode[IntuitError] = {
    case Response(code, _, body) => decode.errorInfo(body) map (IntuitError(op, customer, code, _))
  }

  def decodeError[A, C: Customer](decode: Decoder)(customer: C, op: IntuitOp[A]): PartialDecode[A] = {
    case Response(404, _, _) => NotFoundError(customer, op).left
    case res                 => decodeErrorInfo(decode)(customer, op).apply(res).merge.left
  }

  def decodeSuccess[A](decode: Decoder)(op: IntuitOp[A]): PartialDecode[A] = op match {
    // @formatter:off
    case ListInstitutions                 => { case Response(200, _, body) => decode.institutions(body) }

    case GetInstitution(_)                => { case Response(200, _, body) => decode.institution(body) map (Some(_))
                                               case Response(404, _, _)    => None.right }

    case ListCustomerAccounts |
         ListLoginAccounts(_)             => { case Response(200, _, body) => decode.accounts(body) }

    case GetAccount(_)                    => { case Response(200, _, body) => decode.account(body) }

    case AddAccounts(_, _) |
         AddAccountsChallenge(_, _, _, _) => { case Response(201, _, body) => decode.accounts(body) map (_.right)
                                               case Response(401, h, body) => decode.challengeSession(h.toMap, body) map (ChallengeIssued(_).left)
                                               case Response(_,   _, body) if loginError(decode)(body).isDefined => loginError(decode)(body).get.left.right }

    case DeleteAccount(_)                 => { case Response(200, _, _)    => 1.right
                                               case Response(404, _, _)    => 0.right }

    case UpdateAccountType(_, _)          => { case Response(200, _, _)    => ().right }

    case ListTransactions(_, _, _)        => { case Response(200, _, body) => decode.transactions(body) }

    case ListPositions(_)                 => { case Response(200, _, body) => decode.positions(body) }

    case UpdateLogin(_, _) |
         UpdateLoginChallenge(_, _, _, _) => { case Response(200, _, _)    => ().right.right
                                               case Response(401, h, body) => decode.challengeSession(h.toMap, body) map (ChallengeIssued(_).left)
                                               case Response(_,   _, body) if loginError(decode)(body).isDefined => loginError(decode)(body).get.left.right }

    case DeleteCustomer                   => { case Response(200, _, _)    => 1.right
                                               case Response(404, _, _)    => 0.right }
    // @formatter:on
  }

  def parseResponse[M[_] : Monad : Catchable, C: Customer, A](decode: Decoder)(customer: C, op: IntuitOp[A], response: Response): M[A] =
    (decodeSuccess(decode)(op) orElse decodeError(decode)(customer, op)).apply(response) match {
      case -\/(e) => Catchable[M].fail(e)
      case \/-(a) => Monad[M].point(a)
    }
}
