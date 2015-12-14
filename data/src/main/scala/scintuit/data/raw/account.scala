package scintuit.data.raw

import com.github.nscala_money.money.Imports._
import com.github.nscala_time.time.Imports._
import enumeratum.EnumEntry.Uppercase
import enumeratum.{Enum, EnumEntry}

import scintuit.data.raw.error._
import scintuit.data.raw.institution._
import scintuit.data.raw.login._

/**
 * Module for account resources
 */
object account {

  type AccountId = Long

  sealed abstract class AccountStatus extends EnumEntry with Uppercase
  object AccountStatus extends Enum[AccountStatus] {
    val values = findValues
    case object Active extends AccountStatus
    case object Inactive extends AccountStatus
  }

  // ================================ Account Types ================================
  sealed trait AccountType extends EnumEntry

  sealed abstract class BankingAccountType extends AccountType with Uppercase
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

  sealed abstract class CreditAccountType extends AccountType with Uppercase
  object CreditAccountType extends Enum[CreditAccountType] {
    val values = findValues
    case object CreditCard extends CreditAccountType
    case object LineOfCredit extends CreditAccountType
    case object Other extends CreditAccountType
  }

  sealed abstract class InvestmentAccountType extends AccountType with Uppercase
  object InvestmentAccountType extends Enum[InvestmentAccountType] {
    val values = findValues
    case object Taxable extends InvestmentAccountType
    case object `401K` extends InvestmentAccountType {override def entryName: String = "$401K"}
    // Remove when Intuit fixes extraneous $
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

  sealed abstract class LoanAccountType extends AccountType with Uppercase
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

  sealed abstract class RewardAccountType extends AccountType with Uppercase
  object RewardAccountType extends Enum[RewardAccountType] {
    val values = findValues
    case object Affinity extends RewardAccountType
    case object Airline extends RewardAccountType
    case object Auto extends RewardAccountType
    case object Hotel extends RewardAccountType
    case object Shopping extends RewardAccountType
    case object Other extends RewardAccountType
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
  sealed trait Account {
    val accountId: AccountId
    val status: AccountStatus
    val accountNumber: String
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

  final case class OtherAccount(
    accountId: AccountId,
    status: AccountStatus,
    accountNumber: String,
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
    institutionLoginId: LoginId
  ) extends Account

  final case class BankingAccount(
    accountId: AccountId,
    status: AccountStatus,
    accountNumber: String,
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
  ) extends Account

  final case class CreditAccount(
    accountId: AccountId,
    status: AccountStatus,
    accountNumber: String,
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
  ) extends Account

  final case class InvestmentAccount(
    accountId: AccountId,
    status: AccountStatus,
    accountNumber: String,
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
    investmentAccountType: Option[InvestmentAccountType],
    interestMarginBalance: Option[BigDecimal],
    shortBalance: Option[BigDecimal],
    availableCashBalance: Option[BigDecimal],
    currentBalance: Option[BigDecimal],
    maturityValueAmount: Option[BigDecimal],
    unvestedBalance: Option[BigDecimal],
    vestedBalance: Option[BigDecimal],
    empMatchDeferAmount: Option[BigDecimal],
    empMatchDeferAmountYtd: Option[BigDecimal],
    empMatchAmount: Option[BigDecimal],
    empMatchAmountYtd: Option[BigDecimal],
    empPretaxContribAmount: Option[BigDecimal],
    empPretaxContribAmountYtd: Option[BigDecimal],
    rolloverItd: Option[BigDecimal],
    cashBalanceAmount: Option[BigDecimal],
    initialLoanBalance: Option[BigDecimal],
    loanStartDate: Option[DateTime],
    currentLoanBalance: Option[BigDecimal],
    loanRate: Option[BigDecimal]
  ) extends Account

  final case class LoanAccount(
    accountId: AccountId,
    status: AccountStatus,
    accountNumber: String,
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
    loanDescription: Option[String],
    loanType: Option[LoanAccountType],
    postedDate: Option[DateTime],
    term: Option[String],
    lateFeeAmount: Option[BigDecimal],
    payoffAmount: Option[BigDecimal],
    payoffAmountDate: Option[DateTime],
    referenceNumber: Option[String],
    originalMaturityDate: Option[DateTime],
    taxPayeeName: Option[String],
    principalBalance: Option[BigDecimal],
    escrowBalance: Option[BigDecimal],
    interestRate: Option[BigDecimal],
    interestPeriod: Option[String],
    initialAmount: Option[BigDecimal],
    initialDate: Option[DateTime],
    nextPaymentPrincipalAmount: Option[BigDecimal],
    nextPaymentInterestAmount: Option[BigDecimal],
    nextPayment: Option[BigDecimal],
    nextPaymentDate: Option[DateTime],
    lastPaymentDueDate: Option[DateTime],
    lastPaymentReceiveDate: Option[DateTime],
    lastPaymentAmount: Option[BigDecimal],
    lastPaymentPrincipalAmount: Option[BigDecimal],
    lastPaymentInterestAmount: Option[BigDecimal],
    lastPaymentEscrowAmount: Option[BigDecimal],
    lastPaymentLastFeeAmount: Option[BigDecimal],
    lastPaymentLateCharge: Option[BigDecimal],
    principalPaidYTD: Option[BigDecimal],
    interestPaidYTD: Option[BigDecimal],
    insurancePaidYTD: Option[BigDecimal],
    taxPaidYTD: Option[BigDecimal],
    autoPayEnrolled: Option[Boolean],
    collateral: Option[String],
    currentSchool: Option[String],
    firstPaymentDate: Option[DateTime],
    guarantor: Option[String],
    firstMortgage: Option[Boolean],
    loanPaymentFreq: Option[String],
    paymentMinAmount: Option[BigDecimal],
    originalSchool: Option[String],
    recurringPaymentAmount: Option[BigDecimal],
    lender: Option[String],
    endingBalanceAmount: Option[BigDecimal],
    availableBalanceAmount: Option[BigDecimal],
    loanTermType: Option[LoanTermType],
    noOfPayments: Option[Int],
    balloonAmount: Option[BigDecimal],
    projectedInterest: Option[BigDecimal],
    interestPaidLtd: Option[BigDecimal],
    interestRateType: Option[RateType],
    loanPaymentType: Option[PaymentType],
    remainingPayments: Option[Int]
  ) extends Account

  case class RewardAccount(
    accountId: AccountId,
    status: AccountStatus,
    accountNumber: String,
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
    postedDate: Option[DateTime],
    programType: Option[RewardAccountType],
    originalBalance: Option[BigDecimal],
    currentBalance: Option[BigDecimal],
    rewardQualifyAmountYtd: Option[BigDecimal],
    rewardLifetimeEarned: Option[BigDecimal],
    segmentYtd: Option[BigDecimal]
  ) extends Account

}
