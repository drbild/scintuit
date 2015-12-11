package scintuit.util

import scintuit.Intuit.IntuitOp
import scintuit.Intuit.IntuitOp._
import scintuit.{Customer, IntuitError}
import scintuit.data._
import scintuit.util.interpreter.{Context, Stage}

import scalaz._
import scalaz.syntax.apply._
import scalaz.syntax.either._
import scalaz.syntax.std.option._

object response {

  case class IntuitResponse(
    status: Int,
    headers: Map[String, String],
    body: String
  )

  type PartialDecoder[A] = PartialFunction[IntuitResponse, Exception \/ A]

  trait ResponseDecoder {

    protected def decodeErrorInfo(body: String): Exception \/ ErrorInfo

    protected def decodeChallenges(body: String): Exception \/ Vector[Challenge]

    protected def decodeInstitution(body: String): Exception \/ Institution
    protected def decodeInstitutions(body: String): Exception \/ Vector[InstitutionSummary]
    protected def decodeAccount(body: String): Exception \/ Account
    protected def decodeAccounts(body: String): Exception \/ Vector[Account]
    protected def decodePositions(body: String): Exception \/ Vector[Position]
    protected def decodeTransactions(body: String): Exception \/ TransactionsResponse

    private def decodeChallengeSession(headers: Map[String, String], body: String): Exception \/ ChallengeSession = {
      val sessionId  = headers.get("challengeSessionId") \/> new Exception("Missing header: challengeSessionId")
      val nodeId     = headers.get("challengeNodeId") \/> new Exception("Missing header: challengeNodeId")
      val challenges = decodeChallenges(body)
      (sessionId |@| nodeId |@| challenges)(ChallengeSession(_, _, _))
    }

    // @formatter:off
    val forListInstitutions: PartialDecoder[Vector[InstitutionSummary]] = { case IntuitResponse(200, _, body)  => decodeInstitutions(body) }
    val forGetInstitution: PartialDecoder[Option[Institution]]          = { case IntuitResponse(200, _, body)  => decodeInstitution(body) map (Some(_))
                                                                            case IntuitResponse(404, _, _)     => None.right }
    val forListCustomerAccounts: PartialDecoder[Vector[Account]]        = { case IntuitResponse(200, _, body)  => decodeAccounts(body) }
    val forListLoginAccounts: PartialDecoder[Vector[Account]]           = { case IntuitResponse(200, _, body)  => decodeAccounts(body) }
    val forGetAccount: PartialDecoder[Account]                          = { case IntuitResponse(200, _, body)  => decodeAccount(body) }
    val forAddAccounts: PartialDecoder[LoginError \/ Vector[Account]]   = { case IntuitResponse(201, _, body)  => decodeAccounts(body) map (_.right)
                                                                            case IntuitResponse(401, h, body)  => decodeChallengeSession(h, body) map (ChallengeIssued(_).left) }
    val forUpdateLogin: PartialDecoder[LoginError \/ Unit]              = { case IntuitResponse(200, _, _)     => ().right.right
                                                                            case IntuitResponse(401, h, body)  => decodeChallengeSession(h, body) map (ChallengeIssued(_).left) }
    val forDeleteAccount: PartialDecoder[Int]                           = { case IntuitResponse(200, _, _)     =>  1.right
                                                                            case IntuitResponse(404, _, _)     =>  0.right }
    val forUpdateAccountType: PartialDecoder[Unit]                      = { case IntuitResponse(200, _, _)     => ().right }
    val forListTransactions: PartialDecoder[TransactionsResponse]       = { case IntuitResponse(200, _, body)  => decodeTransactions(body) }
    val forListPositions: PartialDecoder[Vector[Position]]              = { case IntuitResponse(200, _, body)  => decodePositions(body) }
    val forDeleteCustomer: PartialDecoder[Int]                          = { case IntuitResponse(200, _, _)     => 1.right
                                                                            case IntuitResponse(404, _, _)     => 0.right }
    // @formatter:on

    def forErrorInfo[C: Customer](op: IntuitOp[_], customer: C): PartialDecoder[IntuitError] = {
       case IntuitResponse(code, _, body) => decodeErrorInfo(body) map (IntuitError(op, customer, code, _))
    }

    def successDecoder[A](op: IntuitOp[A]): PartialDecoder[A] = op match {
      // @formatter:off
      case ListInstitutions                 => forListInstitutions
      case GetInstitution(_)                => forGetInstitution
      case ListCustomerAccounts             => forListCustomerAccounts
      case ListLoginAccounts(_)             => forListLoginAccounts
      case GetAccount(_)                    => forGetAccount
      case AddAccounts(_, _)                => forAddAccounts
      case AddAccountsChallenge(_, _, _, _) => forAddAccounts
      case DeleteAccount(_)                 => forDeleteAccount
      case UpdateAccountType(_, _)          => forUpdateAccountType
      case ListTransactions(_, _, _)        => forListTransactions
      case ListPositions(_)                 => forListPositions
      case UpdateLogin(_, _)                => forUpdateLogin
      case UpdateLoginChallenge(_, _, _, _) => forUpdateLogin
      case DeleteCustomer                   => forDeleteCustomer
      // @formatter:on
    }

    def errorDecoder[A, C: Customer](op: IntuitOp[A], customer: C): PartialFunction[IntuitResponse, Exception \/ A] = {
      case res => forErrorInfo(op, customer).apply(res).merge.left
    }

    def decode[M[_]: Monad: Catchable, T, C: Customer](response: IntuitResponse): Stage[M, T, C, T] = Stage {
      case Context(op, customer) =>
        (successDecoder(op) orElse errorDecoder(op, customer)).apply(response) match {
          case -\/(e) => Catchable[M].fail(e)
          case \/-(a) => Monad[M].point(a)
        }
    }

  }
}
