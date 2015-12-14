package scintuit.data.raw

import com.github.nscala_money.money.Imports._
import com.github.nscala_time.time.Imports._
import enumeratum.{Enum, EnumEntry}
import enumeratum.EnumEntry.{Snakecase, Uppercase}

import scintuit.data.raw.categorization._
import scintuit.data.raw.security._

/**
 * Module for transaction resources
 */
object transaction {

  type TransactionId = Long

  case class TransactionsResponse(transactions: Vector[Transaction], notRefreshedReason: NotRefreshedReason)

  sealed trait NotRefreshedReason extends EnumEntry with Snakecase with Uppercase
  object NotRefreshedReason extends Enum[NotRefreshedReason] {
    val values = findValues

    case object NotNecessary extends NotRefreshedReason
    case object CredentialsRequired extends NotRefreshedReason
    case object ChallengeResponseRequired extends NotRefreshedReason
    case object Unavailable extends NotRefreshedReason
  }

  sealed abstract class CorrectionAction extends EnumEntry with Uppercase
  object CorrectionAction extends Enum[CorrectionAction] {
    val values = findValues
    case object Replace extends CorrectionAction
    case object Delete extends CorrectionAction
  }

  // ================================== Investment Banking Transaction Types ==================================
  sealed abstract class InvestmentSubAccountType extends EnumEntry with Uppercase
  object InvestmentSubAccountType extends Enum[InvestmentSubAccountType] {
    val values = findValues
    case object Cash extends InvestmentSubAccountType
    case object Margin extends InvestmentSubAccountType
    case object Short extends InvestmentSubAccountType
    case object Other extends InvestmentSubAccountType
  }

  sealed abstract class Banking401KSourceType extends EnumEntry with Uppercase
  object Banking401KSourceType extends Enum[Banking401KSourceType] {
    val values = findValues
    case object PreTax extends Banking401KSourceType
    case object AfterTax extends Banking401KSourceType
    case object Match extends Banking401KSourceType
    case object ProfitSharing extends Banking401KSourceType
    case object Rollover extends Banking401KSourceType
    case object OtherVest extends Banking401KSourceType
    case object OtherNonVest extends Banking401KSourceType
  }

  // ================================== Investment Transaction Types ==================================
  sealed abstract class BuyType extends EnumEntry with Uppercase
  object BuyType extends Enum[BuyType] {
    val values = findValues
    case object Buy extends BuyType
    case object BuyToCover extends BuyType
  }

  sealed abstract class IncomeType extends EnumEntry with Uppercase
  object IncomeType extends Enum[IncomeType] {
    val values = findValues
    case object CapitalGainsLong extends IncomeType {override def entryName: String = "CGLONG"}
    case object CapitalGainsShort extends IncomeType {override def entryName: String = "CGSHORT"}
    case object Dividend extends IncomeType {override def entryName: String = "DIV"}
    case object Interest extends IncomeType
    case object Misc extends IncomeType
  }

  sealed abstract class OptionsAction extends EnumEntry with Uppercase
  object OptionsAction extends Enum[OptionsAction] {
    val values = findValues
    case object Exercise extends OptionsAction
    case object Assign extends OptionsAction
    case object Expire extends OptionsAction
  }

  sealed abstract class OptionsBuyType extends EnumEntry with Uppercase
  object OptionsBuyType extends Enum[OptionsBuyType] {
    val values = findValues
    case object BuyToOpen extends OptionsBuyType
    case object BuyToClose extends OptionsBuyType
  }

  sealed abstract class OptionsSellType extends EnumEntry with Uppercase
  object OptionsSellType extends Enum[OptionsSellType] {
    val values = findValues
    case object SellToOpen extends OptionsSellType
    case object SellToClose extends OptionsSellType
  }

  sealed abstract class PositionType extends EnumEntry with Uppercase
  object PositionType extends Enum[PositionType] {
    val values = findValues
    case object Long extends PositionType
    case object Short extends PositionType
  }

  sealed abstract class TransferAction extends EnumEntry with Uppercase
  object TransferAction extends Enum[TransferAction] {
    val values = findValues
    case object In extends TransferAction
    case object Out extends TransferAction
  }

  sealed abstract class RelatedOptionTransactionType extends EnumEntry with Uppercase
  object RelatedOptionTransactionType extends Enum[RelatedOptionTransactionType] {
    val values = findValues
    case object Spread extends RelatedOptionTransactionType
    case object Straddle extends RelatedOptionTransactionType
    case object None extends RelatedOptionTransactionType
    case object Other extends RelatedOptionTransactionType
  }

  sealed abstract class SecuredType extends EnumEntry with Uppercase
  object SecuredType extends Enum[SecuredType] {
    val values = findValues
    case object Naked extends SecuredType
    case object Covered extends SecuredType
  }

  sealed abstract class SellReason extends EnumEntry with Uppercase
  object SellReason extends Enum[SellReason] {
    val values = findValues
    case object Call extends SellReason
    case object Sell extends SellReason
    case object Maturity extends SellReason
  }

  sealed abstract class SellType extends EnumEntry with Uppercase
  object SellType extends Enum[SellType] {
    val values = findValues
    case object Sell extends SellType
    case object SellShort extends SellType
  }

  // ================================== Transactions ==================================
  sealed trait Transaction {
    val id: TransactionId
    val currencyType: Option[CurrencyUnit]
    val institutionTransactionId: Option[String]
    val correctInstitutionTransactionId: Option[String]
    val correctAction: Option[CorrectionAction]
    val serverTransactionId: Option[String]
    val checkNumber: Option[String]
    val refNumber: Option[String]
    val confirmationNumber: Option[String]
    val payeeId: Option[String]
    val payeeName: Option[String]
    val extendedPayeeName: Option[String]
    val memo: Option[String]
    val `type`: Option[String]
    val valueType: Option[String]
    val currencyRate: Option[BigDecimal]
    val currencyOriginal: Option[Boolean]
    val postedDate: Option[DateTime]
    val userDate: Option[DateTime]
    val availableDate: Option[DateTime]
    val amount: Option[BigDecimal]
    val runningBalanceAmount: Option[BigDecimal]
    val pending: Option[Boolean]
    val categorization: Categorization
  }

  case class BankingTransaction(
    id: TransactionId,
    currencyType: Option[CurrencyUnit],
    institutionTransactionId: Option[String],
    correctInstitutionTransactionId: Option[String],
    correctAction: Option[CorrectionAction],
    serverTransactionId: Option[String],
    checkNumber: Option[String],
    refNumber: Option[String],
    confirmationNumber: Option[String],
    payeeId: Option[String],
    payeeName: Option[String],
    extendedPayeeName: Option[String],
    memo: Option[String],
    `type`: Option[String],
    valueType: Option[String],
    currencyRate: Option[BigDecimal],
    currencyOriginal: Option[Boolean],
    postedDate: Option[DateTime],
    userDate: Option[DateTime],
    availableDate: Option[DateTime],
    amount: Option[BigDecimal],
    runningBalanceAmount: Option[BigDecimal],
    pending: Option[Boolean],
    categorization: Categorization
  ) extends Transaction

  case class CreditTransaction(
    id: TransactionId,
    currencyType: Option[CurrencyUnit],
    institutionTransactionId: Option[String],
    correctInstitutionTransactionId: Option[String],
    correctAction: Option[CorrectionAction],
    serverTransactionId: Option[String],
    checkNumber: Option[String],
    refNumber: Option[String],
    confirmationNumber: Option[String],
    payeeId: Option[String],
    payeeName: Option[String],
    extendedPayeeName: Option[String],
    memo: Option[String],
    `type`: Option[String],
    valueType: Option[String],
    currencyRate: Option[BigDecimal],
    currencyOriginal: Option[Boolean],
    postedDate: Option[DateTime],
    userDate: Option[DateTime],
    availableDate: Option[DateTime],
    amount: Option[BigDecimal],
    runningBalanceAmount: Option[BigDecimal],
    pending: Option[Boolean],
    categorization: Categorization
  ) extends Transaction

  case class RewardTransaction(
    id: TransactionId,
    currencyType: Option[CurrencyUnit],
    institutionTransactionId: Option[String],
    correctInstitutionTransactionId: Option[String],
    correctAction: Option[CorrectionAction],
    serverTransactionId: Option[String],
    checkNumber: Option[String],
    refNumber: Option[String],
    confirmationNumber: Option[String],
    payeeId: Option[String],
    payeeName: Option[String],
    extendedPayeeName: Option[String],
    memo: Option[String],
    `type`: Option[String],
    valueType: Option[String],
    currencyRate: Option[BigDecimal],
    currencyOriginal: Option[Boolean],
    postedDate: Option[DateTime],
    userDate: Option[DateTime],
    availableDate: Option[DateTime],
    amount: Option[BigDecimal],
    runningBalanceAmount: Option[BigDecimal],
    pending: Option[Boolean],
    categorization: Categorization
  ) extends Transaction

  case class LoanTransaction(
    id: TransactionId,
    currencyType: Option[CurrencyUnit],
    institutionTransactionId: Option[String],
    correctInstitutionTransactionId: Option[String],
    correctAction: Option[CorrectionAction],
    serverTransactionId: Option[String],
    checkNumber: Option[String],
    refNumber: Option[String],
    confirmationNumber: Option[String],
    payeeId: Option[String],
    payeeName: Option[String],
    extendedPayeeName: Option[String],
    memo: Option[String],
    `type`: Option[String],
    valueType: Option[String],
    currencyRate: Option[BigDecimal],
    currencyOriginal: Option[Boolean],
    postedDate: Option[DateTime],
    userDate: Option[DateTime],
    availableDate: Option[DateTime],
    amount: Option[BigDecimal],
    runningBalanceAmount: Option[BigDecimal],
    pending: Option[Boolean],
    categorization: Categorization,
    principalAmount: Option[BigDecimal],
    interestAmount: Option[BigDecimal],
    escrowTotalAmount: Option[BigDecimal],
    escrowTaxAmount: Option[BigDecimal],
    escrowInsuranceAmount: Option[BigDecimal],
    escrowPmiAmount: Option[BigDecimal],
    escrowFeesAmount: Option[BigDecimal],
    escrowOtherAmount: Option[BigDecimal]
  ) extends Transaction

  case class InvestmentBankingTransaction(
    id: TransactionId,
    currencyType: Option[CurrencyUnit],
    institutionTransactionId: Option[String],
    correctInstitutionTransactionId: Option[String],
    correctAction: Option[CorrectionAction],
    serverTransactionId: Option[String],
    checkNumber: Option[String],
    refNumber: Option[String],
    confirmationNumber: Option[String],
    payeeId: Option[String],
    payeeName: Option[String],
    extendedPayeeName: Option[String],
    memo: Option[String],
    `type`: Option[String],
    valueType: Option[String],
    currencyRate: Option[BigDecimal],
    currencyOriginal: Option[Boolean],
    postedDate: Option[DateTime],
    userDate: Option[DateTime],
    availableDate: Option[DateTime],
    amount: Option[BigDecimal],
    runningBalanceAmount: Option[BigDecimal],
    pending: Option[Boolean],
    categorization: Categorization,
    bankingString: Option[String],
    subaccountFundType: Option[InvestmentSubAccountType],
    banking401KSourceType: Option[Banking401KSourceType]
  ) extends Transaction

  case class InvestmentTransaction(
    id: TransactionId,
    currencyType: Option[CurrencyUnit],
    institutionTransactionId: Option[String],
    correctInstitutionTransactionId: Option[String],
    correctAction: Option[CorrectionAction],
    serverTransactionId: Option[String],
    checkNumber: Option[String],
    refNumber: Option[String],
    confirmationNumber: Option[String],
    payeeId: Option[String],
    payeeName: Option[String],
    extendedPayeeName: Option[String],
    memo: Option[String],
    `type`: Option[String],
    valueType: Option[String],
    currencyRate: Option[BigDecimal],
    currencyOriginal: Option[Boolean],
    postedDate: Option[DateTime],
    userDate: Option[DateTime],
    availableDate: Option[DateTime],
    amount: Option[BigDecimal],
    runningBalanceAmount: Option[BigDecimal],
    pending: Option[Boolean],
    categorization: Categorization,
    reversalInstitutionTransactionId: Option[String],
    description: Option[String],
    buyType: Option[BuyType],
    incomeType: Option[IncomeType],
    inv401kSource: Option[Banking401KSourceType],
    loanId: Option[String],
    optionsActionType: Option[OptionsAction],
    optionsBuyType: Option[OptionsBuyType],
    optionsSellType: Option[OptionsSellType],
    positionType: Option[PositionType],
    relatedInstitutionTradeId: Option[String],
    relatedOptionTransType: Option[RelatedOptionTransactionType],
    securedType: Option[SecuredType],
    sellReason: Option[SellReason],
    sellType: Option[SellType],
    subaccountFromType: Option[InvestmentSubAccountType],
    subaccountFundType: Option[InvestmentSubAccountType],
    subaccountSecurityType: Option[InvestmentSubAccountType],
    subaccountToType: Option[InvestmentSubAccountType],
    transferAction: Option[TransferAction],
    unitType: Option[String],
    cusip: Option[String],
    symbol: Option[String],
    unitAction: Option[String],
    optionsSecurity: Option[String],
    tradeDate: Option[DateTime],
    settleDate: Option[DateTime],
    accruedInterestAmount: Option[BigDecimal],
    averageCostBasisAmount: Option[BigDecimal],
    commissionAmount: Option[BigDecimal],
    denominator: Option[BigDecimal],
    payrollDate: Option[DateTime],
    purchaseDate: Option[DateTime],
    gainAmount: Option[BigDecimal],
    feesAmount: Option[BigDecimal],
    fractionalUnitsCashAccount: Option[BigDecimal],
    loadAmount: Option[BigDecimal],
    loanInterestAmount: Option[BigDecimal],
    loanPrincipalAmount: Option[BigDecimal],
    markdownAmount: Option[BigDecimal],
    markupAmount: Option[BigDecimal],
    newUnits: Option[BigDecimal],
    numerator: Option[BigDecimal],
    oldUnits: Option[BigDecimal],
    penaltyAmount: Option[BigDecimal],
    priorYearContribution: Option[Boolean],
    sharesPerContract: Option[Long],
    stateWithholding: Option[BigDecimal],
    totalAmount: Option[BigDecimal],
    taxesAmount: Option[BigDecimal],
    taxExempt: Option[Boolean],
    unitPrice: Option[BigDecimal],
    units: Option[BigDecimal],
    withholding: Option[BigDecimal],
    optionsSharesPerContract: Option[Long],
    securityInfo: Option[SecurityInfo]
  ) extends Transaction

}
