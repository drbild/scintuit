package scintuit.util

import com.github.nscala_time.time.Imports._
import scintuit.Customer
import scintuit.Intuit.IntuitOp
import scintuit.Intuit.IntuitOp._
import scintuit.data._
import scintuit.util.interpreter.Stage

import scalaz.Monad

object request {

  private val BASE_URI = "https://financialdatafeed.platform.intuit.com/v1/"

  sealed trait Method
  case object Delete extends Method
  case object Get extends Method
  case object Post extends Method
  case object Put extends Method

  case class IntuitRequest(
    method: Method,
    resource: String,
    headers: Map[String, String],
    body: Option[String]
  ) {
    def uri = BASE_URI + resource

    def withBody(body: String): IntuitRequest =
      this.copy(method, resource, headers, Some(body))

    def withHeader(name: String, value: String): IntuitRequest =
      this.copy(method, resource, headers.updated(name, value), None)
  }

  private def request(m: Method, r: String): IntuitRequest = IntuitRequest(m, r, Map.empty, None)

  private def delete(r: String): IntuitRequest = request(Delete, r)
  private def get(r: String): IntuitRequest = request(Get, r)
  private def post(r: String): IntuitRequest = request(Post, r)
  private def put(r: String): IntuitRequest = request(Put, r)

  trait RequestEncoder {
    def encodeCredentials(credentials: Seq[Credentials]): String
    def encodeAnswers(answers: Seq[ChallengeAnswer]): String
    def encodeType(typ: AccountType): String

    private def formatDate(date: DateTime): String = date.toString("yyyy-MM-dd")

    def encode[M[_]: Monad, T, C: Customer](op: IntuitOp[T]): Stage[M, T, C, IntuitRequest] = Stage { _ =>
      Monad[M] point {
        op match {
          // @formatter:off
          case ListInstitutions                                 => get(s"institutions")
          case GetInstitution(id)                               => get(s"institutions/${id}")
          case ListCustomerAccounts                             => get(s"accounts")
          case ListLoginAccounts(id)                            => get(s"logins/${id}/accounts")
          case GetAccount(id)                                   => get(s"accounts/${id}")
          case AddAccounts(id, credentials)                     => post(s"institutions/${id}/logins")
                                                                       .withBody(encodeCredentials(credentials))
          case AddAccountsChallenge(id, session, node, answers) => post(s"institutions/${id}/logins")
                                                                       .withHeader("challengeSessionId", session)
                                                                       .withHeader("challengeNodeId", node)
                                                                       .withBody(encodeAnswers(answers))
          case DeleteAccount(id)                                => delete(s"accounts/${id}")
          case UpdateAccountType(id, typ)                       => put(s"accounts/${id}")
                                                                      .withBody(encodeType(typ))
          case ListTransactions(id, start, None)                => get(s"accounts/${id}/transactions?txnStartDate=${formatDate(start)}")
          case ListTransactions(id, start, Some(end))           => get(s"accounts/${id}/transactions?txnStartDate=${formatDate(start)}&txnEndDate=${formatDate(end)}")
          case ListPositions(id)                                => get(s"accounts/${id}/positions")
          case UpdateLogin(id, credentials)                     => put(s"logins/${id}")
                                                                      .withBody(encodeCredentials(credentials))
          case UpdateLoginChallenge(id, session, node, answers) => put(s"logins/${id}")
                                                                      .withHeader("challengeSessionId", session)
                                                                      .withHeader("challengeNodeId", node)
                                                                      .withBody(encodeAnswers(answers))
          case DeleteCustomer                                   => delete(s"customers")
          // @formatter:on
        }
      }
    }
  }

}
