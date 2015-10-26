/*
 * Copyright 2015 David R. Bild
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scintuit.data

import com.github.nscala_money.money.Imports._
import com.github.nscala_time.time.Imports._
import enumeratum.EnumEntry._
import enumeratum.{Enum, EnumEntry}

import scalaz.Scalaz._

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

// ================================== Categorization ==================================
sealed abstract class CategorizationSource extends EnumEntry with Uppercase
object CategorizationSource extends Enum[CategorizationSource] {
  val values = findValues
  case object Aggr extends CategorizationSource
  case object OFX extends CategorizationSource
  case object Cat extends CategorizationSource
}

case class RawCategorization(
  common: RawCategorizationCommon,
  context: Vector[RawCategorizationContext]
)

case class RawCategorizationCommon(
  normalizedPayeeName: Option[String],
  merchant: Option[String],
  sic: Option[Int]
)

case class RawCategorizationContext(
  source: Option[CategorizationSource],
  categoryName: Option[String],
  contextType: Option[String],
  scheduleC: Option[String]
)

case class CategorizationContext(raw: RawCategorizationContext) {
  def category: Option[String] = raw.categoryName
  def scheduleC: Option[String] = raw.scheduleC

  def source: Option[CategorizationSource] = raw.source
  def contextType: Option[String] = raw.contextType
}

case class Categorization(raw: RawCategorization) {
  def payee: Option[String] = raw.common.normalizedPayeeName
  def merchant: Option[String] = raw.common.merchant
  def sic: Option[String] = raw.common.sic map (_.toString)

  def contexts: Vector[CategorizationContext] = raw.context map CategorizationContext
}

// ================================== Transactions ==================================
sealed trait RawTransaction {
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
  val categorization: RawCategorization
}

sealed trait Transaction {
  val raw: RawTransaction

  private[data] def toMoney(amount: Option[BigDecimal]): Option[BigMoney] =
    (raw.currencyType |@| amount)(BigMoney.of)

  def id: TransactionId = raw.id
  def idInstitution: Option[String] = raw.institutionTransactionId
  def idServer: Option[String] = raw.serverTransactionId

  def currency: Option[CurrencyUnit] = raw.currencyType
  def currencyRate: Option[BigDecimal] = raw.currencyRate
  def currencyConverted: Option[Boolean] = raw.currencyOriginal

  def amount: Option[BigMoney] = toMoney(raw.amount)
  def pending: Option[Boolean] = raw.pending
  def string: Option[String] = raw.`type`

  def memo: Option[String] = raw.memo
  def categorization: Categorization = Categorization(raw.categorization)
  def payeeId: Option[String] = raw.payeeId
  def payeeName: Option[String] = raw.payeeName
  def payeeNameExtended: Option[String] = raw.extendedPayeeName

  def date: Option[DateTime] = raw.userDate
  def dateAggregated: Option[DateTime] = raw.availableDate
  def datePosted: Option[DateTime] = raw.postedDate

  def numberCheck: Option[String] = raw.checkNumber
  def numberReference: Option[String] = raw.refNumber
  def numberConfirmation: Option[String] = raw.confirmationNumber

  def correctionAction: Option[CorrectionAction] = raw.correctAction
  def valueType: Option[String] = raw.valueType
}

final case class RawBankingTransaction(
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
  categorization: RawCategorization
) extends RawTransaction

final case class BankingTransaction(raw: RawBankingTransaction) extends Transaction

final case class RawCreditTransaction(
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
  categorization: RawCategorization
) extends RawTransaction

final case class CreditTransaction(raw: RawCreditTransaction) extends Transaction

final case class RawRewardTransaction(
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
  categorization: RawCategorization
) extends RawTransaction

final case class RewardTransaction(raw: RawRewardTransaction) extends Transaction

final case class RawLoanTransaction(
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
  categorization: RawCategorization,
  principalAmount: Option[BigDecimal],
  interestAmount: Option[BigDecimal],
  escrowTotalAmount: Option[BigDecimal],
  escrowTaxAmount: Option[BigDecimal],
  escrowInsuranceAmount: Option[BigDecimal],
  escrowPmiAmount: Option[BigDecimal],
  escrowFeesAmount: Option[BigDecimal],
  escrowOtherAmount: Option[BigDecimal]
) extends RawTransaction

final case class LoanTransaction(raw: RawLoanTransaction) extends Transaction {
  def amountPrincipal: Option[BigMoney] = toMoney(raw.principalAmount)
  def amountInterest: Option[BigMoney] = toMoney(raw.interestAmount)

  def amountEscrowFees: Option[BigMoney] = toMoney(raw.escrowFeesAmount)
  def amountEscrowInsurance: Option[BigMoney] = toMoney(raw.escrowInsuranceAmount)
  def amountEscrowOther: Option[BigMoney] = toMoney(raw.escrowOtherAmount)
  def amountEscrowPMI: Option[BigMoney] = toMoney(raw.escrowPmiAmount)
  def amountEscrowTax: Option[BigMoney] = toMoney(raw.escrowTaxAmount)
  def amountEscrowTotal: Option[BigMoney] = toMoney(raw.escrowTotalAmount)
}

final case class RawInvestmentBankingTransaction(
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
  categorization: RawCategorization,
  bankingString: Option[String],
  subaccountFundType: Option[InvestmentSubAccountType],
  banking401KSourceType: Option[Banking401KSourceType]
) extends RawTransaction

final case class InvestmentBankingTransaction(raw: RawInvestmentBankingTransaction) extends Transaction {
  def bankingString: Option[String] = raw.bankingString
  def banking401KSourceType: Option[Banking401KSourceType] = raw.banking401KSourceType
  def bankingSubAccountType: Option[InvestmentSubAccountType] = raw.subaccountFundType
}

final case class RawInvestmentTransaction(
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
  categorization: RawCategorization,
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
  optionsSharesPerContract: Option[Long]
) extends RawTransaction


final case class InvestmentTransaction(raw: RawInvestmentTransaction, rawInfo: Option[RawSecurityInfo])  extends
Transaction {
  def idReversalInstitutionTransaction: Option[String] = raw.reversalInstitutionTransactionId
  def idRelatedInstitutionTrade: Option[String] = raw.relatedInstitutionTradeId
  def idLoan: Option[String] = raw.loanId

  def description: Option[String] = raw.description
  def cusip: Option[String] = raw.cusip
  def symbol: Option[String] = raw.symbol
  def taxExempt: Option[Boolean] = raw.taxExempt

  def amountTotal: Option[BigMoney] = toMoney(raw.totalAmount)
  def amountCommission: Option[BigMoney] = toMoney(raw.commissionAmount)
  def amountFees: Option[BigMoney] = toMoney(raw.feesAmount)
  def amountGain: Option[BigMoney] = toMoney(raw.gainAmount)
  def amountLoad: Option[BigMoney] = toMoney(raw.loadAmount)
  def amountPenalty: Option[BigMoney] = toMoney(raw.penaltyAmount)
  def amountTax: Option[BigMoney] = toMoney(raw.taxesAmount)
  def amountWithholding: Option[BigMoney] = toMoney(raw.withholding)
  def amountWithholdingState: Option[BigMoney] = toMoney(raw.stateWithholding)
  def amountCashFractionalUnits: Option[BigMoney] = toMoney(raw.fractionalUnitsCashAccount)

  def datePayroll: Option[DateTime] = raw.payrollDate
  def datePurchase: Option[DateTime] = raw.purchaseDate
  def dateSettle: Option[DateTime] = raw.settleDate
  def dateTrade: Option[DateTime] = raw.tradeDate

  def units: Option[BigDecimal] = raw.units
  def unitsNew: Option[BigDecimal] = raw.newUnits
  def unitsOld: Option[BigDecimal] = raw.oldUnits

  def unitPrice: Option[BigMoney] = toMoney(raw.unitPrice)
  def unitPriceMarkdown: Option[BigMoney] = toMoney(raw.markdownAmount)
  def unitPriceMarkup: Option[BigMoney] = toMoney(raw.markupAmount)
  def unitAction: Option[String] = raw.unitAction
  def unitType: Option[String] = raw.unitType

  def contributionPriorYear: Option[Boolean] = raw.priorYearContribution
  def costBasisAverage: Option[BigMoney] = toMoney(raw.averageCostBasisAmount)
  def sharesPerContract: Option[Long] = raw.sharesPerContract

  def interestAccrued: Option[BigMoney] = toMoney(raw.accruedInterestAmount)

  def loanInterest: Option[BigMoney] = toMoney(raw.loanInterestAmount)
  def loanPrincipal: Option[BigMoney] = toMoney(raw.loanPrincipalAmount)

  def incomeType: Option[IncomeType] = raw.incomeType
  def positionType: Option[PositionType] = raw.positionType
  def securedType: Option[SecuredType] = raw.securedType

  def buyType: Option[BuyType] = raw.buyType
  def sellType: Option[SellType] = raw.sellType
  def sellReason: Option[SellReason] = raw.sellReason
  def transferAction: Option[TransferAction] = raw.transferAction

  def optionsAction: Option[OptionsAction] = raw.optionsActionType
  def optionsBuyType: Option[OptionsBuyType] = raw.optionsBuyType
  def optionsSellType: Option[OptionsSellType] = raw.optionsSellType
  def optionsSecurity: Option[String] = raw.optionsSecurity
  def optionsSharesPerContract: Option[Long] = raw.optionsSharesPerContract

  def splitNumerator: Option[BigDecimal] = raw.numerator
  def splitDenominator: Option[BigDecimal] = raw.denominator

  def banking401KSourceType: Option[Banking401KSourceType] = raw.inv401kSource
  def relatedOptionTransactionType: Option[RelatedOptionTransactionType] = raw.relatedOptionTransType

  def subaccountFromType: Option[InvestmentSubAccountType] = raw.subaccountFromType
  def subaccountToType: Option[InvestmentSubAccountType] = raw.subaccountToType
  def subaccountFundType: Option[InvestmentSubAccountType] = raw.subaccountFundType
  def subaccountSecurityType: Option[InvestmentSubAccountType] = raw.subaccountSecurityType

  def securityInfo: Option[SecurityInfo] = rawInfo map (SecurityInfo.fromRaw)
}

// ====================== Responses ======================
final case class TransactionsResponse(transactions: Vector[Transaction], notRefreshedReason: NotRefreshedReason)
