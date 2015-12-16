package scintuit.api

import org.joda.time.DateTime

import scintuit.data.api.account._
import scintuit.data.api.institution._
import scintuit.data.api.login._
import scintuit.data.api.position._
import scintuit.data.api.transaction._
import scintuit.data.raw
import scintuit.raw.{customer => I}

import scala.language.postfixOps
import scalaz.\/
import scalaz.syntax.monad._
import scalaz.syntax.traverse._

/**
 * Module of better API constructors for `IntuitIO` actions.
 */
object customer {

  type Customer[T] = I.Customer[T]
  val Customer = I.Customer

  type CustomerIO[A] = I.CustomerIO[A]

  implicit val MonadCustomerIO = I.MonadCustomerIO

  // ---------------------------- Institutions ----------------------------
  val institutions: CustomerIO[Vector[InstitutionSummary]] =
    I.getInstitutions map (_ map InstitutionSummary)

  def institution(id: InstitutionId): CustomerIO[Option[Institution]] =
    I.getInstitutionDetails(id) map (_ map Institution)

  // ------------------------------- Logins -------------------------------
  private def loginId(accounts: Vector[Account]): LoginId =
    accounts match { case account +: _ => account.loginId }

  private def institutionId(accounts: Vector[Account]): InstitutionId =
    accounts match { case account +: _ => account.institutionId }

  private def fromRaw(accounts: Vector[RawAccount]): Vector[Account] =
    accounts map Account.apply

  def logins: CustomerIO[Vector[Login]] =
    accounts map { as =>
      (as groupBy (a => (a.loginId, a.institutionId)) toVector) map { case ((l, i), as) => Login(l, i, as) }
    }

  def login(id: LoginId): CustomerIO[Login] =
    I.getLoginAccounts(id) map fromRaw map (as => Login(loginId(as), institutionId(as), as))

  def login(login: Login): CustomerIO[Login] =
    this.login(login.id)

  def loginByInstitution(id: InstitutionId): CustomerIO[Login] =
    accounts map (_ filter (_.institutionId == id)) map (as => Login(loginId(as), institutionId(as), as))

  def addLogin(id: InstitutionId, credentials: Seq[Credentials]): CustomerIO[LoginError \/ Login] =
    I.addAccounts(id, credentials) >>= (_ traverseU (fromRaw _ andThen loginId _ andThen login _))

  def addLogin(institution: Institution, credentials: Seq[Credentials]): CustomerIO[LoginError \/ Login] =
    addLogin(institution.id, credentials)

  def addLogin(id: InstitutionId, sessionId: ChallengeSessionId, nodeId: ChallengeNodeId, answers: Seq[ChallengeAnswer]): CustomerIO[LoginError \/ Login] =
    I.addAccounts(id, sessionId, nodeId, answers) >>= (_ traverseU (fromRaw _ andThen loginId _ andThen login _))

  def addLogin(institution: Institution, sessionId: ChallengeSessionId, nodeId: ChallengeNodeId, answers: Seq[ChallengeAnswer]): CustomerIO[LoginError \/ Login] =
    addLogin(institution.id, sessionId, nodeId, answers)

  def updateLogin(id: LoginId, credentials: Seq[Credentials]): CustomerIO[LoginError \/ Login] =
    I.updateLogin(id, credentials) map (_ map (_ => id)) >>= (_ traverseU login)

  def updateLogin(login: Login, credentials: Seq[Credentials]): CustomerIO[LoginError \/ Login] =
    updateLogin(login.id, credentials)

  def updateLogin(id: LoginId, sessionId: ChallengeSessionId, nodeId: ChallengeNodeId, answers: Seq[ChallengeAnswer]): CustomerIO[LoginError \/ Login] =
    I.updateLogin(id, sessionId, nodeId, answers) map (_ map (_ => id)) >>= (_ traverseU login)

  def updateLogin(login: Login, sessionId: ChallengeSessionId, nodeId: ChallengeNodeId, answers: Seq[ChallengeAnswer]): CustomerIO[LoginError \/ Login] =
    updateLogin(login.id, sessionId, nodeId, answers)

  // ------------------------------ Accounts ------------------------------
  val accounts: CustomerIO[Vector[Account]] =
    I.getCustomerAccounts map (_ map Account.apply)

  def account(id: AccountId): CustomerIO[Account] =
    I.getAccount(id) map Account.apply

  def account(account: Account): CustomerIO[Account] =
    this.account(account.id)

  def updateAccount(id: AccountId, accountType: AccountType): CustomerIO[Account] =
    I.updateAccountType(id, accountType) >> account(id)

  def updateAccount(account: Account, accountType: AccountType): CustomerIO[Account] =
    updateAccount(account.id, accountType)

  def deleteAccount(id: AccountId): CustomerIO[Int] =
    I.deleteAccount(id)

  def deleteAccount(account: Account): CustomerIO[Int] =
    deleteAccount(account.id)

  // ------------------------------ Transactions ------------------------------
  def transactions(id: AccountId, start: DateTime, end: Option[DateTime]): CustomerIO[Vector[Transaction]] =
    I.getTransactions(id, start, end) map (_.transactions map Transaction.fromRaw)

  def transactions(account: Account, start: DateTime, end: Option[DateTime]): CustomerIO[Vector[Transaction]] =
    transactions(account.id, start, end)

  def transactions(id: AccountId, start: DateTime, end: DateTime): CustomerIO[Vector[Transaction]] =
    transactions(id, start, Some(end))

  def transactions(account: Account, start: DateTime, end: DateTime): CustomerIO[Vector[Transaction]] =
    transactions(account.id, start, end)

  def transactions(id: AccountId, start: DateTime): CustomerIO[Vector[Transaction]] =
    transactions(id, start, None)

  def transactions(account: Account, start: DateTime): CustomerIO[Vector[Transaction]] =
    transactions(account.id, start)

  // ------------------------------ Positions ------------------------------
  def positions(id: AccountId): CustomerIO[Vector[Position]] =
    I.getPositions(id) map (_ map Position)

  def positions(account: Account): CustomerIO[Vector[Position]] =
    positions(account.id)

  // ----------------------------- Customers ------------------------------
  def delete(): CustomerIO[Int] =
    I.delete
}
