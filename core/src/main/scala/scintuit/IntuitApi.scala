package scintuit

import com.github.nscala_time.time.Imports._
import scintuit.data._

import scalaz._

object IntuitApi {
  /**
   * ADT for Intuit API operations
   */
  sealed trait IntuitOp[A]

  object IntuitOp {
    // ---------------------------- Institutions ----------------------------
    case object ListInstitutions extends IntuitOp[Vector[InstitutionSummary]]
    case class GetInstitution(id: InstitutionId) extends IntuitOp[Institution]

    // ------------------------------- Logins -------------------------------
    case class AddAccounts(
      id: InstitutionId,
      credentials: Seq[Credentials]) extends IntuitOp[LoginError \/ AddAccountsResponse]

    case class AddAccountsChallenge(
      id: InstitutionId,
      sessionId: ChallengeSessionId,
      nodeId: ChallengeNodeId,
      answers: Seq[ChallengeAnswer]) extends IntuitOp[ChallengeError \/ AddAccountsResponse]

    case class UpdateLogin(
      id: LoginId,
      credentials: Seq[Credentials]) extends IntuitOp[LoginError \/ UpdateLoginResponse]

    case class UpdateLoginChallenge(
      id: LoginId,
      sessionId: ChallengeSessionId,
      nodeId: ChallengeNodeId,
      answers: Seq[ChallengeAnswer]) extends IntuitOp[ChallengeError \/ UpdateLoginResponse]

    // ------------------------------ Accounts ------------------------------
    case class GetAccount(id: AccountId) extends IntuitOp[Account]
    case class DeleteAccount(id: AccountId) extends IntuitOp[Unit]
    case object ListCustomerAccounts extends IntuitOp[Vector[Account]]
    case class ListLoginAccounts(id: LoginId) extends IntuitOp[Vector[Account]]

    // ---------------------------- Transactions ----------------------------
    case class ListTransactions(
      id: AccountId,
      start: DateTime,
      end: Option[DateTime]) extends IntuitOp[TransactionsResponse]

    // ------------------------------ Positions -----------------------------
    case class ListPositions(id: AccountId) extends IntuitOp[Vector[Position]]

    // ----------------------------- Customers ------------------------------
    case object DeleteCustomer extends IntuitOp[Unit]
  }

  import IntuitOp._

  /**
   * Free monad over a free functor of [[IntuitOp]].
   */
  type IntuitIO[A] = Free.FreeC[IntuitOp, A]

  /**
   * Monad instance for [[IntuitIO]] (can't be be inferred).
   */
  implicit val MonadIntuitIO: Monad[IntuitIO] = Free.freeMonad[({type λ[α] = Coyoneda[IntuitOp, α]})#λ]

  // ---------------------------- Institutions ----------------------------
  val institutions: IntuitIO[Vector[InstitutionSummary]] = Free.liftFC(ListInstitutions)
  def institution(id: Long): IntuitIO[Institution] = Free.liftFC(GetInstitution(id))

  // ------------------------------- Logins -------------------------------
  def addAccounts(id: InstitutionId, credentials: Seq[Credentials]): IntuitIO[LoginError \/ AddAccountsResponse] =
    Free.liftFC(AddAccounts(id, credentials))

  def addAccounts(
    id: InstitutionId,
    sessionId: ChallengeSessionId,
    nodeId: ChallengeNodeId,
    answers: Seq[ChallengeAnswer]): IntuitIO[ChallengeError \/ AddAccountsResponse] =
    Free.liftFC(AddAccountsChallenge(id, sessionId, nodeId, answers))

  def updateLogin(id: LoginId, credentials: Seq[Credentials]): IntuitIO[LoginError \/ UpdateLoginResponse] =
    Free.liftFC(UpdateLogin(id, credentials))

  def updateLogin(
    id: InstitutionId,
    sessionId: ChallengeSessionId,
    nodeId: ChallengeNodeId,
    answers: Seq[ChallengeAnswer]): IntuitIO[ChallengeError \/ UpdateLoginResponse] =
    Free.liftFC(UpdateLoginChallenge(id, sessionId, nodeId, answers))

  // ------------------------------ Accounts ------------------------------
  def account(id: AccountId): IntuitIO[Account] = Free.liftFC(GetAccount(id))
  val accounts: IntuitIO[Vector[Account]] = Free.liftFC(ListCustomerAccounts)
  def accounts(id: LoginId): IntuitIO[Vector[Account]] = Free.liftFC(ListLoginAccounts(id))
  def deleteAccount(id: AccountId): IntuitIO[Unit] = Free.liftFC(DeleteAccount(id))

  // ------------------------------ Transactions ------------------------------
  def transactions(id: AccountId, start: DateTime) = Free.liftFC(ListTransactions(id, start, None))
  def transactions(id: AccountId, start: DateTime, end: DateTime) = Free.liftFC(ListTransactions(id, start, Some(end)))

  // ------------------------------ Positions ------------------------------
  def positions(id: AccountId): IntuitIO[Vector[Position]] = Free.liftFC(ListPositions(id))

  // ----------------------------- Customers ------------------------------
  def deleteCustomer: IntuitIO[Unit] = Free.liftFC(DeleteCustomer)
}
