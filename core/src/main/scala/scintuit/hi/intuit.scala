package scintuit.hi

import scintuit.data._
import scintuit.free.{intuit => I}
import org.joda.time.DateTime

import scalaz.\/
import scalaz.syntax.monad._
import scalaz.syntax.traverse._

/**
 * Module of better API constructors for `IntuitIO` actions.
 */
object intuit {

  type IntuitIO[A] = I.IntuitIO[A]

  implicit val MonadIntuitIO = I.MonadIntuitIO

  // ---------------------------- Institutions ----------------------------
  val institutions: IntuitIO[Vector[InstitutionSummary]] =
    I.getInstitutions

  def institution(id: InstitutionId): IntuitIO[Option[Institution]] =
    I.getInstitutionDetails(id)

  // ------------------------------- Logins -------------------------------
  def logins: IntuitIO[Vector[Login]] =
   accounts map (_.groupBy(_.loginId).toVector.map(Login.tupled))

  def login(id: LoginId): IntuitIO[Login] =
    I.getLoginAccounts(id) map (Login(id, _))

  def login(login: Login): IntuitIO[Login] =
    this.login(login.id)

  private def loginId(accounts: Vector[Account]): LoginId = accounts match { case account +: _ => account.loginId }

  def addLogin(id: InstitutionId, credentials: Seq[Credentials]): IntuitIO[LoginError \/ Login] =
    I.addAccounts(id, credentials) >>= (_ traverseU (loginId _ andThen login _))

  def addLogin(institution: Institution, credentials: Seq[Credentials]): IntuitIO[LoginError \/ Login] =
    addLogin(institution.id, credentials)

  def addLogin(id: InstitutionId, sessionId: ChallengeSessionId, nodeId: ChallengeNodeId, answers: Seq[ChallengeAnswer]): IntuitIO[LoginError \/ Login] =
    I.addAccounts(id, sessionId, nodeId, answers) >>= (_ traverseU (loginId _ andThen login _))

  def addLogin(institution: Institution, sessionId: ChallengeSessionId, nodeId: ChallengeNodeId, answers: Seq[ChallengeAnswer]): IntuitIO[LoginError \/ Login] =
    addLogin(institution.id, sessionId, nodeId, answers)

  def updateLogin(id: LoginId, credentials: Seq[Credentials]): IntuitIO[LoginError \/ Login] =
    I.updateLogin(id, credentials) map (_ map (_ => id)) >>= (_ traverseU login)

  def updateLogin(login: Login, credentials: Seq[Credentials]): IntuitIO[LoginError \/ Login] =
    updateLogin(login.id, credentials)

  def updateLogin(id: LoginId, sessionId: ChallengeSessionId, nodeId: ChallengeNodeId, answers: Seq[ChallengeAnswer]): IntuitIO[LoginError \/ Login] =
    I.updateLogin(id, sessionId, nodeId, answers) map (_ map (_ => id)) >>= (_ traverseU login)

  def updateLogin(login: Login, sessionId: ChallengeSessionId, nodeId: ChallengeNodeId, answers: Seq[ChallengeAnswer]): IntuitIO[LoginError \/ Login] =
    updateLogin(login.id, sessionId, nodeId, answers)

  // ------------------------------ Accounts ------------------------------
  val accounts: IntuitIO[Vector[Account]] =
    I.getCustomerAccounts

  def account(id: AccountId): IntuitIO[Account] =
    I.getAccount(id)

  def account(account: Account): IntuitIO[Account] =
    this.account(account.id)

  def updateAccount(id: AccountId, accountType: AccountType): IntuitIO[Account] =
    I.updateAccountType(id, accountType) >> account(id)

  def updateAccount(account: Account, accountType: AccountType): IntuitIO[Account] =
    updateAccount(account.id, accountType)

  def deleteAccount(id: AccountId): IntuitIO[Int] =
    I.deleteAccount(id)

  def deleteAccount(account: Account): IntuitIO[Int] =
    deleteAccount(account.id)

  // ------------------------------ Transactions ------------------------------
  def transactions(id: AccountId, start: DateTime, end: Option[DateTime]): IntuitIO[Vector[Transaction]] =
    I.getTransactions(id, start, end) map (_.transactions)

  def transactions(account: Account, start: DateTime, end: Option[DateTime]): IntuitIO[Vector[Transaction]] =
    transactions(account.id, start, end)

  def transactions(id: AccountId, start: DateTime, end: DateTime): IntuitIO[Vector[Transaction]] =
    transactions(id, start, Some(end))

  def transactions(account: Account, start: DateTime, end: DateTime): IntuitIO[Vector[Transaction]] =
    transactions(account.id, start, end)

  def transactions(id: AccountId, start: DateTime): IntuitIO[Vector[Transaction]] =
    transactions(id, start, None)

  def transactions(account: Account, start: DateTime): IntuitIO[Vector[Transaction]] =
    transactions(account.id, start)

  // ------------------------------ Positions ------------------------------
  def positions(id: AccountId): IntuitIO[Vector[Position]] =
    I.getPositions(id)

  def positions(account: Account): IntuitIO[Vector[Position]] =
    positions(account.id)

  // ----------------------------- Customers ------------------------------
  def deleteCustomer: IntuitIO[Int] =
    I.deleteCustomer
}
