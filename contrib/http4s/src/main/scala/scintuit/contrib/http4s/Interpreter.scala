package scintuit
package contrib.http4s

import com.github.nscala_time.time.Imports._
import org.http4s.Http4s._
import org.http4s.Status.ResponseClass._
import org.http4s.Status._
import org.http4s.client.Client
import org.http4s.{DateTime => _, _}
import play.api.libs.json._
import scintuit.IntuitApi.IntuitOp._
import scintuit.IntuitApi._
import scintuit.auth.{IntuitConfig, OAuthConsumer, OAuthToken}
import scintuit.contrib.http4s.contrib.play.PlayJsonInstances
import scintuit.contrib.play.all._
import scintuit.contrib.play.ApiTransforms
import scintuit.data.{Challenge, _}

import scalaz._
import scalaz.concurrent.Task
import scalaz.std.option._
import scalaz.syntax.ToIdOps
import scalaz.syntax.either._
import scalaz.syntax.monad._


object Interpreter extends PlayJsonInstances with ApiTransforms with ToIdOps {

  case class Scope(client: Client, consumer: OAuthConsumer, token: OAuthToken)

  // ================================ Helpers ================================
  private object HandlingErrors {
    val all = apply _
    val none = identity[Client] _

    def apply[A](client: Client): Client = new Client {
      override def shutdown(): Task[Unit] = client.shutdown

      override def prepare(req: Request): Task[Response] = {
        client.prepare(req) flatMap { res =>
          if (res.status.code >= 400)
            res.as(jsonOf(implicitly[Reads[ErrorInfo]] compose errorInfoT)) flatMap { e =>
              Task.fail(IntuitError(req.uri.renderString, res.status.code, e))
            }
          else
            res.point[Task]
        }
      }
    }
  }

  private val BASE = uri("https://financialdatafeed.platform.intuit.com/v1")

  private def get(uri: Uri): Request = Request(Method.GET, uri)
  private def get(path: String): Request = get(BASE / path)

  private def post(uri: Uri): Request = Request(Method.POST, uri)
  private def put(uri: Uri): Request = Request(Method.PUT, uri)

  private def delete(uri: Uri): Request = Request(Method.DELETE, uri)
  private def delete(path: String): Request = delete(BASE / path)

  /** Apply a transform to the JsValue before Reading */
  private def decode[A](transform: Reads[JsValue])(implicit reads: Reads[A]): EntityDecoder[A] =
    jsonOf(reads compose transform)

  /** Apply a transform to the JsValue after Writing */
  private def encode[A](transform: Writes[JsValue])(implicit writes: Writes[A]): EntityEncoder[A] =
    jsonEncoderOf(writes transform transform)

  private def decodeChallengeIssued(res: Response): Task[ChallengeIssued] =
    res.as[Vector[Challenge]](decode(challengeIssuedT)) flatMap
      (decodeChallengeSession(res.headers, _).map(ChallengeIssued(_)))

  private def decodeChallengeSession(headers: Headers, challenges: Vector[Challenge]): Task[ChallengeSession] = {
    val session = headers.get("challengeSessionId".ci) map (_.value)
    val node = headers.get("challengeNodeId".ci) map (_.value)
    (session |@| node)(ChallengeSession(_, _, challenges)) match {
      case None => Task.fail(new RuntimeException("Missing challenge session id or challenge node id."))
      case Some(c) => Task.now(c)
    }
  }

  // ================================ Natural Transforms ================================
  type IntuitK[A] = Kleisli[Task, Scope, A]

  def interpK: IntuitOp ~> IntuitK =
    new (IntuitOp ~> IntuitK) {
      def apply[A](op: IntuitOp[A]): IntuitK[A] = Kleisli[Task, Scope, A] { scope =>
        val client = scope.client |> Accepting.json |> OAuth.signing(scope.consumer, scope.token)
        val handlingAll = client |> HandlingErrors.all
        def handlingNone = client |> HandlingErrors.none

        def format(date: DateTime): String = date.toString("yyyy-MM-dd")

        op match {
          case ListInstitutions =>
            handlingAll(get(s"institutions")).as[Vector[InstitutionSummary]](decode(listInstitutionsT))

          case GetInstitution(id) =>
            handlingAll(get(s"institutions/${id}")).as[Institution](decode(getInstitutionT))

          case ListCustomerAccounts =>
            handlingAll(get(s"accounts")).as[Vector[Account]](decode(listAccountsT))

          case ListLoginAccounts(id) =>
            handlingAll(get(s"logins/${id}/accounts")).as[Vector[Account]](decode(listAccountsT))

          case GetAccount(id) =>
            handlingAll(get(s"accounts/${id}")).as[Account](decode(getAccountT))

          case AddAccounts(id, credentials) =>
            val uri = (BASE / s"institutions/${id}/logins")
            val req = post(uri).withBody(credentials)(encode(addAccountsT))
            handlingNone(req) flatMap {
              case Successful(res) => res.as[Vector[Account]](decode(listAccountsT)) map (AccountsAdded(_).right)
              case Unauthorized(res) => decodeChallengeIssued(res) map (_.right)//res.as[Vector[Challenge]](decode(challengeIssuedT)) flatMap
              case res => res.as[ErrorInfo](decode(errorInfoT)) flatMap {
                case InvalidCredentials(code) => Task.now(InvalidCredentials(code).left)
                case InterventionRequired(code) => Task.now(InterventionRequired(code).left)
                case error => Task.fail(IntuitError(uri.renderString, res.status.code, error))
              }
            }

          case AddAccountsChallenge(id, session, node, answers) =>
            val uri = (BASE / s"institutions/${id}/logins")
            val req = post(uri).withBody(answers)(encode(addAccountsChallengeT))
              .putHeaders(Header("challengeSessionId", session), Header("challengeNodeId", node))
            handlingNone(req) flatMap {
              case Successful(res) => res.as[Vector[Account]](decode(listAccountsT)) map (AccountsAdded(_).right)
              case Unauthorized(res) => decodeChallengeIssued(res) map (_.right)//res.as[Vector[Challenge]](decode(challengeIssuedT)) flatMap
              case res => res.as[ErrorInfo](decode(errorInfoT)) flatMap {
                case IncorrectChallengeAnswer(code) => Task.now(IncorrectChallengeAnswer(code).left)
                case InterventionRequired(code) => Task.now(InterventionRequired(code).left)
                case error => Task.fail(IntuitError(uri.renderString, res.status.code, error))
              }
            }

          case DeleteAccount(id) =>
            handlingAll(delete(s"accounts/${id}")) map (_ => ())

          case ListTransactions(id, start, end) =>
            val uri = (BASE / s"accounts/${id}/transactions")
              .+?("txtStartDate", start |> format)
              .+??("txnEndDate", end map format)
            handlingAll(get(uri)).as[TransactionsResponse](decode(idT))

          case ListPositions(id) =>
            handlingAll(get(s"accounts/${id}/positions")).as[Vector[Position]](decode(listPositionsT))

          case UpdateLogin(id, credentials) =>
            val uri = (BASE / s"logins/${id}") +? ("refresh", "true")
            val req = put(uri).withBody(credentials)(encode(addAccountsT))
            handlingNone(req) flatMap {
              case Successful(res) => Task.now(LoginUpdated.right)
              case Unauthorized(res) => decodeChallengeIssued(res) map (_.right)
              case res => res.as[ErrorInfo](decode(errorInfoT)) flatMap {
                case InvalidCredentials(code) => Task.now(InvalidCredentials(code).left)
                case InterventionRequired(code) => Task.now(InterventionRequired(code).left)
                case error => Task.fail(IntuitError(uri.renderString, res.status.code, error))
              }
            }

          case UpdateLoginChallenge(id, session, node, answers) =>
            val uri = (BASE / s"logins/${id}")
            val req = put(uri).withBody(answers)(encode(addAccountsChallengeT))
              .putHeaders(Header("challengeSessionId", session), Header("challengeNodeId", node))
            handlingNone(req) flatMap {
              case Successful(res) => Task.now(LoginUpdated.right)
              case Unauthorized(res) => decodeChallengeIssued(res) map (_.right)
              case res => res.as[ErrorInfo](decode(errorInfoT)) flatMap {
                case IncorrectChallengeAnswer(code) => Task.now(IncorrectChallengeAnswer(code).left)
                case InterventionRequired(code) => Task.now(InterventionRequired(code).left)
                case error => Task.fail(IntuitError(uri.renderString, res.status.code, error))
              }
            }

          case DeleteCustomer =>
            handlingAll(delete(s"customers")) map (_ => ())
        }
      }
    }

  def transK: IntuitIO ~> IntuitK = new (IntuitIO ~> IntuitK) {
    def apply[A](ma: IntuitIO[A]): IntuitK[A] = Free.runFC[IntuitOp, IntuitK, A](ma)(interpK)
  }

  def trans(scope: Task[Scope]): IntuitIO ~> Task = new (IntuitIO ~> Task) {
    def apply[A](ma: IntuitIO[A]): Task[A] = scope flatMap (transK(ma).run(_))
  }

  def trans(client: Client, config: IntuitConfig, customer: Customer): IntuitIO ~> Task = {
    val scope = OAuth.tokenForCustomer(client, config, customer) map (Scope(client, config.oauthConsumer, _))
    trans(scope)
  }
}
