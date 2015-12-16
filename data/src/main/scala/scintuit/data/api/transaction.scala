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

package scintuit.data.api

import com.github.nscala_money.money.Imports._
import com.github.nscala_time.time.Imports._
import enumeratum.EnumEntry._
import enumeratum.{Enum, EnumEntry}

import scintuit.data.api.categorization._
import scintuit.data.api.security._
import scintuit.data.raw

import scalaz.std.option._
import scalaz.syntax.apply._

/**
 * Module for categorization types
 */
object transaction {

  type TransactionId = raw.transaction.TransactionId

  type CorrectionAction = raw.transaction.CorrectionAction
  val CorrectionAction = raw.transaction.CorrectionAction

  // ================================== Investment Banking Transaction Types ==================================
  type InvestmentSubAccountType = raw.transaction.InvestmentSubAccountType
  type Banking401KSourceType = raw.transaction.Banking401KSourceType

  val InvestmentSubAccountType = raw.transaction.InvestmentSubAccountType
  val Banking401KSourceType = raw.transaction.Banking401KSourceType

  // ================================== Investment Transaction Types ==================================
  type BuyType = raw.transaction.BuyType
  type IncomeType = raw.transaction.IncomeType
  type OptionsAction = raw.transaction.OptionsAction
  type OptionsBuyType = raw.transaction.OptionsBuyType
  type OptionsSellType = raw.transaction.OptionsSellType
  type PositionType = raw.transaction.PositionType
  type TransferAction = raw.transaction.TransferAction
  type RelatedOptionTransactionType = raw.transaction.RelatedOptionTransactionType
  type SecuredType = raw.transaction.SecuredType
  type SellReason = raw.transaction.SellReason
  type SellType = raw.transaction.SellType

  val BuyType  = raw.transaction.BuyType
  val IncomeType = raw.transaction.IncomeType
  val OptionsAction = raw.transaction.OptionsAction
  val OptionsBuyType = raw.transaction.OptionsBuyType
  val OptionsSellType = raw.transaction.OptionsSellType
  val PositionType = raw.transaction.PositionType
  val TransferAction = raw.transaction.TransferAction
  val RelatedOptionTransactionType = raw.transaction.RelatedOptionTransactionType
  val SecuredType = raw.transaction.SecuredType
  val SellReason = raw.transaction.SellReason
  val SellType = raw.transaction.SellType

  // ================================== Transactions ==================================
  type RawTransaction = raw.transaction.Transaction
  type RawBankingTransaction = raw.transaction.BankingTransaction
  type RawCreditTransaction = raw.transaction.CreditTransaction
  type RawLoanTransaction = raw.transaction.LoanTransaction
  type RawInvestmentBankingTransaction = raw.transaction.InvestmentBankingTransaction
  type RawInvestmentTransaction = raw.transaction.InvestmentTransaction
  type RawRewardTransaction = raw.transaction.RewardTransaction

  sealed abstract class TransactionType(private[transaction] val priority: Int) extends EnumEntry with Uppercase {

    /**
     * We can detect transaction types using multiple methods, e.g., the raw type field and the Intuit
     * consumer category.  The type priority is used to settle disagreement among these methods.
     */
    private[transaction] def merge(typ: TransactionType): TransactionType =
      if (typ.priority > this.priority) typ else this

  }


  object TransactionType extends Enum[TransactionType] {
    val values = findValues

    case object ATM extends TransactionType(10)
    case object Cash extends TransactionType(10)
    case object Check extends TransactionType(10)
    case object Credit extends TransactionType(1)
    case object Debit extends TransactionType(1)
    case object Deposit extends TransactionType(1) { override def entryName: String = "DEP" }
    case object DirectDebit extends TransactionType(10) { override def entryName: String = "DIRECTDEBIT" }
    case object DirectDeposit extends TransactionType(10) { override def entryName: String = "DIRECTDEP" }
    case object Dividend extends TransactionType(10) { override def entryName: String = "DIV" }
    case object Interest extends TransactionType(10) { override def entryName: String = "INT" }
    case object Fee extends TransactionType(10)
    case object Other extends TransactionType(1)
    case object Payment extends TransactionType(1)
    case object POS extends TransactionType(1)
    case object RepeatedPayment extends TransactionType(1) { override def entryName: String = "REPEATPMT" }
    case object ServiceCharge extends TransactionType(10) { override def entryName: String = "SVGCHG" }
    case object Transfer extends TransactionType(1) { override def entryName: String = "XFER" }

    def withAlternateNameInsensitiveOption(alt: String): Option[TransactionType] =
      alt match {
        case "dividend" => Some(TransactionType.Dividend)
        case "interest" => Some(TransactionType.Interest)
        case _ => None
      }

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
    def amountAbsolute: Option[BigMoney] = toMoney(raw.amount map (_.abs))
    def pending: Option[Boolean] = raw.pending orElse datePosted.map(_ => false)

    def `type`: Option[TransactionType] =
      // @formatter:off
      Vector(
        raw.`type` flatMap TransactionType.withNameInsensitiveOption,
        raw.`type` flatMap TransactionType.withAlternateNameInsensitiveOption,
        if (numberCheck.isDefined) Some(TransactionType.Check) else None,
        if (isInCategory(ConsumerCategory.Check)) Some(TransactionType.Check) else None,
        if (isInCategory(ConsumerCategory.InterestIncome)) Some(TransactionType.Interest) else None,
        if (isInCategory(ConsumerCategory.FeesAndCharges)) Some(TransactionType.Fee) else None
      ).flatten reduceOption (_ merge _)
      // @formatter:on

    def categorization: Categorization = Categorization(raw.categorization)
    def categories: Set[ConsumerCategory] = categorization.categories
    def scheduleCs: Set[String] = categorization.scheduleCs

    def isInCategory(category: ConsumerCategory): Boolean =
      categories exists (_ isA category)

    def memo: Option[String] = raw.memo
    def merchant: Option[String] = categorization.merchant

    def payee: Option[String] = merchant orElse payeeNameNormalized orElse payeeName
    def payeeId: Option[String] = raw.payeeId
    def payeeName: Option[String] = raw.payeeName
    def payeeNameExtended: Option[String] = raw.extendedPayeeName
    def payeeNameNormalized: Option[String] = categorization.payee

    def date: Option[DateTime] = raw.userDate orElse datePosted
    def dateAggregated: Option[DateTime] = raw.availableDate
    def datePosted: Option[DateTime] = raw.postedDate

    def numberCheck: Option[String] = raw.checkNumber
    def numberReference: Option[String] = raw.refNumber
    def numberConfirmation: Option[String] = raw.confirmationNumber

    def correctionAction: Option[CorrectionAction] = raw.correctAction
    def valueType: Option[String] = raw.valueType
  }

  object Transaction {
    def fromRaw(raw: RawTransaction): Transaction = raw match {
      case t: RawBankingTransaction => BankingTransaction(t)
      case t: RawCreditTransaction => CreditTransaction(t)
      case t: RawInvestmentTransaction => InvestmentTransaction(t)
      case t: RawInvestmentBankingTransaction => InvestmentBankingTransaction(t)
      case t: RawLoanTransaction => LoanTransaction(t)
      case t: RawRewardTransaction => RewardTransaction(t)
    }
  }

  case class BankingTransaction(raw: RawBankingTransaction) extends Transaction
  case class CreditTransaction(raw: RawCreditTransaction) extends Transaction
  case class RewardTransaction(raw: RawRewardTransaction) extends Transaction

  case class LoanTransaction(raw: RawLoanTransaction) extends Transaction {
    def amountPrincipal: Option[BigMoney] = toMoney(raw.principalAmount)
    def amountInterest: Option[BigMoney] = toMoney(raw.interestAmount)

    def amountEscrowFees: Option[BigMoney] = toMoney(raw.escrowFeesAmount)
    def amountEscrowInsurance: Option[BigMoney] = toMoney(raw.escrowInsuranceAmount)
    def amountEscrowOther: Option[BigMoney] = toMoney(raw.escrowOtherAmount)
    def amountEscrowPMI: Option[BigMoney] = toMoney(raw.escrowPmiAmount)
    def amountEscrowTax: Option[BigMoney] = toMoney(raw.escrowTaxAmount)
    def amountEscrowTotal: Option[BigMoney] = toMoney(raw.escrowTotalAmount)
  }

  case class InvestmentBankingTransaction(raw: RawInvestmentBankingTransaction) extends Transaction {
    def bankingString: Option[String] = raw.bankingString
    def banking401KSourceType: Option[Banking401KSourceType] = raw.banking401KSourceType
    def bankingSubAccountType: Option[InvestmentSubAccountType] = raw.subaccountFundType
  }

  case class InvestmentTransaction(raw: RawInvestmentTransaction) extends Transaction {
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

    def security: Option[Security] = raw.securityInfo map Security.fromRaw
  }

}
