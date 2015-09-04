package scintuit

import scintuit.data._

import scalaz._

object IntuitApi {
  /**
   * ADT for Intuit API operations
   */
  sealed trait IntuitOp[A]

  object IntuitOp {
    // ---------------------------- Institutions ----------------------------
    case object ListInstitutions extends IntuitOp[Seq[InstitutionSummary]]
    case class GetInstitution(id: InstitutionId) extends IntuitOp[Institution]

    // ------------------------------- Logins -------------------------------
    case class AddAccounts(
      id: InstitutionId,
      credentials: Seq[Credentials]
    ) extends IntuitOp[LoginError \/ AddAccountsResponse]

    case class AddAccountsChallenge(
      sessionId: ChallengeSessionId,
      nodeId: ChallengeNodeId,
      answers: Seq[ChallengeAnswer]
    ) extends IntuitOp[ChallengeError \/ AddAccountsResponse]

    case class UpdateLogin(
      id: LoginId,
      credentials: Seq[Credentials]
    ) extends IntuitOp[LoginError \/ UpdateLoginResponse]

    case class UpdateLoginChallenge(
      sessionId: ChallengeSessionId,
      nodeId: ChallengeNodeId,
      answers: Seq[ChallengeAnswer]
    ) extends IntuitOp[ChallengeError \/ UpdateLoginResponse]

    // ------------------------------ Accounts ------------------------------
    case class GetAccount(id: AccountId) extends IntuitOp[Account]
    case class DeleteAccount(id: AccountId) extends IntuitOp[Unit]
    case object ListCustomerAccounts extends IntuitOp[Seq[Account]]
    case class ListLoginAccounts(id: LoginId) extends IntuitOp[Seq[Account]]

    // ---------------------------- Transactions ----------------------------
    //case class ListTransactions(id: AccountId) extends IntuitOp[Seq[Transaction]]

    // ----------------------------- Customers ------------------------------
    //case object DeleteCustomer extends IntuitOp[Unit]

    // ---------------------------- Institutions ----------------------------
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
  val institutions: IntuitIO[Seq[InstitutionSummary]] = Free.liftFC(ListInstitutions)
  def institution(id: Long): IntuitIO[Institution] = Free.liftFC(GetInstitution(id))

  // ------------------------------- Logins -------------------------------
  def addAccounts(id: InstitutionId, credentials: Seq[Credentials]): IntuitIO[LoginError \/ AddAccountsResponse] =
    Free.liftFC(AddAccounts(id, credentials))

  def addAccounts(
    sessionId: ChallengeSessionId,
    nodeId: ChallengeNodeId,
    answers: Seq[ChallengeAnswer]): IntuitIO[ChallengeError \/ AddAccountsResponse] =
    Free.liftFC(AddAccountsChallenge(sessionId, nodeId, answers))

  def updateLogin(id: LoginId, credentials: Seq[Credentials]): IntuitIO[LoginError \/ UpdateLoginResponse] =
    Free.liftFC(UpdateLogin(id, credentials))

  def updateLogin(
    sessionId: ChallengeSessionId,
    nodeId: ChallengeNodeId,
    answers: Seq[ChallengeAnswer]): IntuitIO[ChallengeError \/ UpdateLoginResponse] =
    Free.liftFC(UpdateLoginChallenge(sessionId, nodeId, answers))

  // ------------------------------ Accounts ------------------------------
  def account(id: AccountId): IntuitIO[Account] = Free.liftFC(GetAccount(id))
  val accounts: IntuitIO[Seq[Account]] = Free.liftFC(ListCustomerAccounts)
  def accounts(id: LoginId): IntuitIO[Seq[Account]] = Free.liftFC(ListLoginAccounts(id))
  def deleteAccount(id: AccountId): IntuitIO[Unit] = Free.liftFC(DeleteAccount(id))
}
