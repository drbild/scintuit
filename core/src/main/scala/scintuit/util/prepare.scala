package scintuit.util

import com.github.nscala_time.time.Imports._
import scintuit.raw.intuit.IntuitOp
import scintuit.raw.intuit.IntuitOp._
import scintuit.data._

object prepare {

  trait Encoder {
    def credentials(credentials: Seq[Credentials]): String
    def answers(answers: Seq[ChallengeAnswer]): String
    def accountType(typ: AccountType): String
  }

  import http._

  private val BASE_URI = "https://financialdatafeed.platform.intuit.com/v1/"

  private def formatDate(date: DateTime): String = date.toString("yyyy-MM-dd")

  private def request(m: Method, r: String) =
    http.request(m, BASE_URI + r)
      .withHeader("Content-Type", "application/json")
      .withHeader("Accept", "application/json")

  private def delete(r: String): Request = request(DELETE, r)
  private def get(r: String): Request = request(GET, r)
  private def post(r: String): Request = request(POST, r)
  private def put(r: String): Request = request(PUT, r)

  def prepareRequest[T](encode: Encoder)(op: IntuitOp[T]): Request = op match {
    // @formatter:off
    case ListInstitutions                                 => get(s"institutions")
    case GetInstitution(id)                               => get(s"institutions/$id")
    case ListCustomerAccounts                             => get(s"accounts")
    case ListLoginAccounts(id)                            => get(s"logins/$id/accounts")
    case GetAccount(id)                                   => get(s"accounts/$id")
    case AddAccounts(id, credentials)                     => post(s"institutions/$id/logins")
                                                                 .withBody(encode.credentials(credentials))
    case AddAccountsChallenge(id, session, node, answers) => post(s"institutions/$id/logins")
                                                                 .withHeader("challengeSessionId", session)
                                                                 .withHeader("challengeNodeId", node)
                                                                 .withBody(encode.answers(answers))
    case DeleteAccount(id)                                => delete(s"accounts/$id")
    case UpdateAccountType(id, typ)                       => put(s"accounts/$id")
                                                                .withBody(encode.accountType(typ))
    case ListTransactions(id, start, None)                => get(s"accounts/$id/transactions?txnStartDate=${formatDate(start)}")
    case ListTransactions(id, start, Some(end))           => get(s"accounts/$id/transactions?txnStartDate=${formatDate(start)}&txnEndDate=${formatDate(end)}")
    case ListPositions(id)                                => get(s"accounts/$id/positions")
    case UpdateLogin(id, credentials)                     => put(s"logins/$id")
                                                                .withBody(encode.credentials(credentials))
    case UpdateLoginChallenge(id, session, node, answers) => put(s"logins/$id")
                                                                .withHeader("challengeSessionId", session)
                                                                .withHeader("challengeNodeId", node)
                                                                .withBody(encode.answers(answers))
    case DeleteCustomer                                   => delete(s"customers")
    // @formatter:on
  }

}
