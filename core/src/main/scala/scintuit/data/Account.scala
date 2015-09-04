package scintuit.data

import com.github.nscala_money.money.Imports._
import com.github.nscala_time.time.Imports._
import enumeratum.EnumEntry._
import enumeratum.{Enum, EnumEntry}

import scalaz.Scalaz._

sealed abstract class AccountStatus extends EnumEntry with Uppercase
object AccountStatus extends Enum[AccountStatus] {
  val values = findValues
  case object Active extends AccountStatus
  case object Inactive extends AccountStatus
}

// ================================ Account Types ================================
sealed abstract class BankingAccountType extends EnumEntry with Uppercase
object BankingAccountType extends Enum[BankingAccountType] {
  val values = findValues
  case object Checking extends BankingAccountType
  case object Savings extends BankingAccountType
  case object MonkeyMarket extends BankingAccountType {override def entryName: String = "MONEYMRKT"}
  case object RecurringDeposit extends BankingAccountType
  case object CD extends BankingAccountType
  case object CashManagement extends BankingAccountType
  case object Overdraft extends BankingAccountType
}

sealed abstract class CreditAccountType extends EnumEntry with Uppercase
object CreditAccountType extends Enum[CreditAccountType] {
  val values = findValues
  case object CreditCard extends CreditAccountType
  case object LineOfCredit extends CreditAccountType
  case object Other extends CreditAccountType
}

sealed abstract class InvestmentAccountType extends EnumEntry with Uppercase
object InvestmentAccountType extends Enum[InvestmentAccountType] {
  val values = findValues
  case object Taxable extends InvestmentAccountType
  case object `401K` extends InvestmentAccountType
  case object Brokerage extends InvestmentAccountType
  case object IRA extends InvestmentAccountType
  case object `403B` extends InvestmentAccountType
  case object KEOGH extends InvestmentAccountType
  case object Trust extends InvestmentAccountType
  case object TDA extends InvestmentAccountType
  case object Simple extends InvestmentAccountType
  case object Normal extends InvestmentAccountType
  case object SARSEP extends InvestmentAccountType
  case object UGMA extends InvestmentAccountType
  case object Other extends InvestmentAccountType
}

sealed abstract class LoanAccountType extends EnumEntry with Uppercase
object LoanAccountType extends Enum[LoanAccountType] {
  val values = findValues
  case object Loan extends LoanAccountType
  case object Auto extends LoanAccountType
  case object Commercial extends LoanAccountType
  case object Construction extends LoanAccountType {override def entryName = "CONSTR"}
  case object Consumer extends LoanAccountType
  case object HomeEquity extends LoanAccountType
  case object Military extends LoanAccountType
  case object Mortgage extends LoanAccountType
  case object SmallBusiness extends LoanAccountType {override def entryName = "SMB"}
  case object Student extends LoanAccountType
}

// ============================ Loan Property Types ============================
sealed abstract class RateType extends EnumEntry with Uppercase
object RateType extends Enum[RateType] {
  val values = findValues
  case object Fixed extends RateType
  case object Floating extends RateType
  case object ARM extends RateType
}

sealed abstract class PaymentType extends EnumEntry
object PaymentType extends Enum[PaymentType] {
  val values = findValues
  case object InterestOnly extends PaymentType {override def entryName = "INT_ONLY"}
  case object PrincipalAndInterest extends PaymentType {override def entryName = "PRN_AND_INT"}
  case object PrincipalPlusInterest extends PaymentType {override def entryName = "PRN_PLUS_INT"}
}

sealed abstract class LoanTermType extends EnumEntry with Uppercase
object LoanTermType extends Enum[LoanTermType] {
  val values = findValues
  case object Combo extends LoanTermType
  case object Fixed extends LoanTermType
  case object Revolve extends LoanTermType
  case object Open extends LoanTermType
}

// ================================== Accounts ==================================
sealed trait RawAccount {
  val accountId: AccountId
  val status: AccountStatus
  val accountNickname: Option[String]
  val displayPosition: Option[Int]
  val institutionId: InstitutionId
  val description: Option[String]
  val balanceAmount: Option[BigDecimal]
  val balanceDate: Option[DateTime]
  val balancePreviousAmount: Option[BigDecimal]
  val lastTxnDate: Option[DateTime]
  val aggrSuccessDate: Option[DateTime]
  val aggrAttemptDate: Option[DateTime]
  val aggrStatusCode: Option[ErrorCode]
  val currencyCode: Option[CurrencyUnit]
  val institutionLoginId: LoginId
}

sealed trait Account {
  val raw: RawAccount

  private[data] def toMoney(amount: Option[BigDecimal]): Option[BigMoney] =
    (raw.currencyCode |@| amount)(BigMoney.of)

  def id: AccountId = raw.accountId
  def institutionId: InstitutionId = raw.institutionId
  def loginId: LoginId = raw.institutionLoginId

  def nickname: Option[String] = raw.accountNickname
  def description: Option[String] = raw.description
  def currency: Option[CurrencyUnit] = raw.currencyCode

  def balance: Option[BigMoney] = toMoney(raw.balanceAmount)
  def balanceDate: Option[DateTime] = raw.balanceDate
  def balancePrevious: Option[BigMoney] = toMoney(raw.balancePreviousAmount)

  def lastTransactionDate: Option[DateTime] = raw.lastTxnDate

  def status: AccountStatus = raw.status
  def displayPosition: Option[Int] = raw.displayPosition

  def aggregationStatus: Option[ErrorCode] = raw.aggrStatusCode
  def aggregationAttemptDate: Option[DateTime] = raw.aggrAttemptDate
  def aggregationSuccessDate: Option[DateTime] = raw.aggrSuccessDate
}

final case class RawBankingAccount(
  accountId: AccountId,
  status: AccountStatus,
  accountNickname: Option[String],
  displayPosition: Option[Int],
  institutionId: InstitutionId,
  description: Option[String],
  balanceAmount: Option[BigDecimal],
  balanceDate: Option[DateTime],
  balancePreviousAmount: Option[BigDecimal],
  lastTxnDate: Option[DateTime],
  aggrSuccessDate: Option[DateTime],
  aggrAttemptDate: Option[DateTime],
  aggrStatusCode: Option[ErrorCode],
  currencyCode: Option[CurrencyUnit],
  institutionLoginId: LoginId,
  bankingAccountType: Option[BankingAccountType],
  postedDate: Option[DateTime],
  availableBalanceAmount: Option[BigDecimal],
  interestType: Option[String],
  originationDate: Option[DateTime],
  openDate: Option[DateTime],
  periodInterestRate: Option[BigDecimal],
  periodDepositAmount: Option[BigDecimal],
  periodInterestAmount: Option[BigDecimal],
  interestAmountYtd: Option[BigDecimal],
  interestPriorAmountYtd: Option[BigDecimal],
  maturityDate: Option[DateTime],
  maturityAmount: Option[BigDecimal]
) extends RawAccount

final case class BankingAccount(raw: RawBankingAccount) extends Account {
  def accountType: Option[BankingAccountType] = raw.bankingAccountType
  def openDate: Option[DateTime] = raw.openDate
  def originationDate: Option[DateTime] = raw.originationDate

  def balanceAvailable: Option[BigMoney] = toMoney(raw.availableBalanceAmount)

  def depositPeriod: Option[BigMoney] = toMoney(raw.periodDepositAmount)

  def interestType: Option[String] = raw.interestType
  def interestRatePeriod: Option[BigDecimal] = raw.periodInterestRate
  def interestPeriod: Option[BigMoney] = toMoney(raw.periodInterestAmount)
  def interestYTD: Option[BigMoney] = toMoney(raw.interestAmountYtd)
  def interestPriorYTD: Option[BigMoney] = toMoney(raw.interestPriorAmountYtd)

  def maturityDate: Option[DateTime] = raw.maturityDate
  def maturityAmount: Option[BigMoney] = toMoney(raw.maturityAmount)

  def transactionsRefreshDate: Option[DateTime] = raw.postedDate
}

final case class RawCreditAccount(
  accountId: AccountId,
  status: AccountStatus,
  accountNickname: Option[String],
  displayPosition: Option[Int],
  institutionId: InstitutionId,
  description: Option[String],
  balanceAmount: Option[BigDecimal],
  balanceDate: Option[DateTime],
  balancePreviousAmount: Option[BigDecimal],
  lastTxnDate: Option[DateTime],
  aggrSuccessDate: Option[DateTime],
  aggrAttemptDate: Option[DateTime],
  aggrStatusCode: Option[ErrorCode],
  currencyCode: Option[CurrencyUnit],
  institutionLoginId: LoginId,
  creditAccountType: Option[CreditAccountType],
  detailedDescription: Option[String],
  interestRate: Option[BigDecimal],
  creditAvailableAmount: Option[BigDecimal],
  creditMaxAmount: Option[BigDecimal],
  cashAdvanceAvailableAmount: Option[BigDecimal],
  cashAdvanceMaxAmount: Option[BigDecimal],
  cashAdvanceBalance: Option[BigDecimal],
  cashAdvanceInterestRate: Option[BigDecimal],
  currentBalance: Option[BigDecimal],
  paymentMinAmount: Option[BigDecimal],
  paymentDueDate: Option[DateTime],
  previousBalance: Option[BigDecimal],
  statementEndDate: Option[DateTime],
  statementPurchaseAmount: Option[BigDecimal],
  statementFinanceAmount: Option[BigDecimal],
  pastDueAmount: Option[BigDecimal],
  lastPaymentAmount: Option[BigDecimal],
  lastPaymentDate: Option[DateTime],
  statementCloseBalance: Option[BigDecimal],
  statementLateFeeAmount: Option[BigDecimal]
) extends RawAccount

final case class CreditAccount(raw: RawCreditAccount) extends Account {
  def accountType: Option[CreditAccountType] = raw.creditAccountType
  def descriptionDetails: Option[String] = raw.detailedDescription

  def creditBalance: Option[BigMoney] = toMoney(raw.currentBalance)
  def creditBalancePrevious: Option[BigMoney] = toMoney(raw.balancePreviousAmount)
  def creditAvailable: Option[BigMoney] = toMoney(raw.creditAvailableAmount)
  def creditMax: Option[BigMoney] = toMoney(raw.creditMaxAmount)
  def creditRate: Option[BigDecimal] = raw.interestRate

  def cashAdvanceBalance: Option[BigMoney] = toMoney(raw.cashAdvanceBalance)
  def cashAdvanceAvailable: Option[BigMoney] = toMoney(raw.cashAdvanceAvailableAmount)
  def cashAdvanceMax: Option[BigMoney] = toMoney(raw.cashAdvanceMaxAmount)
  def cashAdvanceRate: Option[BigDecimal] = raw.cashAdvanceInterestRate

  def paymentMin: Option[BigMoney] = toMoney(raw.paymentMinAmount)
  def paymentDueDate: Option[DateTime] = raw.paymentDueDate
  def paymentPrevious: Option[BigMoney] = toMoney(raw.lastPaymentAmount)
  def paymentPreviousDate: Option[DateTime] = raw.lastPaymentDate

  def pastDueAmount: Option[BigMoney] = toMoney(raw.pastDueAmount)

  def statementEndDate: Option[DateTime] = raw.statementEndDate
  def statementCloseBalance: Option[BigMoney] = toMoney(raw.statementCloseBalance)
  def statementPurchaseAmount: Option[BigMoney] = toMoney(raw.statementPurchaseAmount)
  def statementFinanceAmount: Option[BigMoney] = toMoney(raw.statementFinanceAmount)
  def statementLateFeeAmount: Option[BigMoney] = toMoney(raw.statementLateFeeAmount)
}

//case class TemplateRawAccount(
//  accountId: AccountId,
//  status: String,
//  accountNickname: Option[String],
//  displayPosition: Option[Int],
//  institutionId: InstitutionId,
//  description: Option[String],
//  balanceAmount: Option[BigDecimal],
//  balanceDate: Option[DateTime],
//  balancePreviousAmount: Option[BigDecimal],
//  lastTxnDate: Option[DateTime],
//  aggrSuccessDate: Option[DateTime],
//  aggrAttemptDate: Option[DateTime],
//  aggrStatusCode: Option[ErrorCode],
//  currencyCode: Option[CurrencyUnit],
//  institutionLoginId: Option[LoginId]
//) extends RawAccount
