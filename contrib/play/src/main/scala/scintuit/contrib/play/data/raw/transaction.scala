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

package scintuit.contrib.play.data.raw

import com.github.nscala_money.money.Imports._
import com.github.nscala_money.money.json.PlayImports._
import com.github.nscala_time.time.Imports._
import enumeratum.EnumFormats
import org.cvogt.play.json.Jsonx
import org.cvogt.play.json.implicits.optionWithNull
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scintuit.data.raw.categorization._
import scintuit.data.raw.security._
import scintuit.data.raw.transaction._


object transaction {
  object TransactionFormats extends TransactionFormats

  trait TransactionFormats {
    import categorization.CategorizationFormats._
    import security.SecurityFormats

    implicit val notRefreshedReasonFormat: Format[NotRefreshedReason] = EnumFormats.formats(NotRefreshedReason, insensitive = false)
    implicit val correctionActionFormat: Format[CorrectionAction] = EnumFormats.formats(CorrectionAction, insensitive = false)

    // =========================== Investment Banking Transaction Type Formats =================
    implicit val investmentSubAccountTypeFormat: Format[InvestmentSubAccountType] = EnumFormats.formats(InvestmentSubAccountType, insensitive = false)
    implicit val banking401KSourceTypeFormat: Format[Banking401KSourceType] = EnumFormats.formats(Banking401KSourceType, insensitive = false)

    // =========================== Investment Transaction Type Formats =================
    implicit val buyTypeFormat: Format[BuyType] = EnumFormats.formats(BuyType, insensitive = false)
    implicit val incomeTypeFormat: Format[IncomeType] = EnumFormats.formats(IncomeType, insensitive = false)
    implicit val optionsActionFormat: Format[OptionsAction] = EnumFormats.formats(OptionsAction, insensitive = false)
    implicit val optionsBuyTypeFormat: Format[OptionsBuyType] = EnumFormats.formats(OptionsBuyType, insensitive = false)
    implicit val optionsSellTypeFormat: Format[OptionsSellType] = EnumFormats.formats(OptionsSellType, insensitive = false)
    implicit val positionTypeFormat: Format[PositionType] = EnumFormats.formats(PositionType, insensitive = false)
    implicit val transferTypeFormat: Format[TransferAction] = EnumFormats.formats(TransferAction, insensitive = false)
    implicit val relatedOptionTransactionTypeFormat: Format[RelatedOptionTransactionType] = EnumFormats.formats(RelatedOptionTransactionType, insensitive = false)
    implicit val securedTypeFormat: Format[SecuredType] = EnumFormats.formats(SecuredType, insensitive = false)
    implicit val sellReasonFormat: Format[SellReason] = EnumFormats.formats(SellReason, insensitive = false)
    implicit val sellTypeFormat: Format[SellType] = EnumFormats.formats(SellType, insensitive = false)

    // =========================== Specific Transaction Formats =================
    implicit val bankingTransactionFormat: Format[BankingTransaction] = Jsonx.formatCaseClass[BankingTransaction]
    implicit val creditTransactionFormat: Format[CreditTransaction] = Jsonx.formatCaseClass[CreditTransaction]
    implicit val loanTransactionFormat: Format[LoanTransaction] = Jsonx.formatCaseClass[LoanTransaction]
    implicit val rewardTransactionFormat: Format[RewardTransaction] = Jsonx.formatCaseClass[RewardTransaction]
    implicit val investmentBankingTransactionFormat: Format[InvestmentBankingTransaction] = Jsonx.formatCaseClass[InvestmentBankingTransaction]

    private case class PartialInvestmentTransaction(
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
      optionsSharesPerContract: Option[Long]
    )

    private val investmentTransactionReads: Reads[InvestmentTransaction] = {
      val pt: Reads[PartialInvestmentTransaction] = Jsonx.formatCaseClass[PartialInvestmentTransaction]
      val si: Reads[Option[SecurityInfo]] = SecurityFormats.securityInfoFormat
      (pt and si){(pt, si) =>
        InvestmentTransaction(
          pt.id,
          pt.currencyType,
          pt.institutionTransactionId,
          pt.correctInstitutionTransactionId,
          pt.correctAction,
          pt.serverTransactionId,
          pt.checkNumber,
          pt.refNumber,
          pt.confirmationNumber,
          pt.payeeId,
          pt.payeeName,
          pt.extendedPayeeName,
          pt.memo,
          pt.`type`,
          pt.valueType,
          pt.currencyRate,
          pt.currencyOriginal,
          pt.postedDate,
          pt.userDate,
          pt.availableDate,
          pt.amount,
          pt.runningBalanceAmount,
          pt.pending,
          pt.categorization,
          pt.reversalInstitutionTransactionId,
          pt.description,
          pt.buyType,
          pt.incomeType,
          pt.inv401kSource,
          pt.loanId,
          pt.optionsActionType,
          pt.optionsBuyType,
          pt.optionsSellType,
          pt.positionType,
          pt.relatedInstitutionTradeId,
          pt.relatedOptionTransType,
          pt.securedType,
          pt.sellReason,
          pt.sellType,
          pt.subaccountFromType,
          pt.subaccountFundType,
          pt.subaccountSecurityType,
          pt.subaccountToType,
          pt.transferAction,
          pt.unitType,
          pt.cusip,
          pt.symbol,
          pt.unitAction,
          pt.optionsSecurity,
          pt.tradeDate,
          pt.settleDate,
          pt.accruedInterestAmount,
          pt.averageCostBasisAmount,
          pt.commissionAmount,
          pt.denominator,
          pt.payrollDate,
          pt.purchaseDate,
          pt.gainAmount,
          pt.feesAmount,
          pt.fractionalUnitsCashAccount,
          pt.loadAmount,
          pt.loanInterestAmount,
          pt.loanPrincipalAmount,
          pt.markdownAmount,
          pt.markupAmount,
          pt.newUnits,
          pt.numerator,
          pt.oldUnits,
          pt.penaltyAmount,
          pt.priorYearContribution,
          pt.sharesPerContract,
          pt.stateWithholding,
          pt.totalAmount,
          pt.taxesAmount,
          pt.taxExempt,
          pt.unitPrice,
          pt.units,
          pt.withholding,
          pt.optionsSharesPerContract,
          si)}
    }

    private val investmentTransactionWrites: Writes[InvestmentTransaction] = {
      val pt: Writes[PartialInvestmentTransaction] = Jsonx.formatCaseClass[PartialInvestmentTransaction]
      val si: Writes[Option[SecurityInfo]] = SecurityFormats.securityInfoFormat
      (__.write(pt) and __.write(si)){t =>
        (PartialInvestmentTransaction(
           t.id,
           t.currencyType,
           t.institutionTransactionId,
           t.correctInstitutionTransactionId,
           t.correctAction,
           t.serverTransactionId,
           t.checkNumber,
           t.refNumber,
           t.confirmationNumber,
           t.payeeId,
           t.payeeName,
           t.extendedPayeeName,
           t.memo,
           t.`type`,
           t.valueType,
           t.currencyRate,
           t.currencyOriginal,
           t.postedDate,
           t.userDate,
           t.availableDate,
           t.amount,
           t.runningBalanceAmount,
           t.pending,
           t.categorization,
           t.reversalInstitutionTransactionId,
           t.description,
           t.buyType,
           t.incomeType,
           t.inv401kSource,
           t.loanId,
           t.optionsActionType,
           t.optionsBuyType,
           t.optionsSellType,
           t.positionType,
           t.relatedInstitutionTradeId,
           t.relatedOptionTransType,
           t.securedType,
           t.sellReason,
           t.sellType,
           t.subaccountFromType,
           t.subaccountFundType,
           t.subaccountSecurityType,
           t.subaccountToType,
           t.transferAction,
           t.unitType,
           t.cusip,
           t.symbol,
           t.unitAction,
           t.optionsSecurity,
           t.tradeDate,
           t.settleDate,
           t.accruedInterestAmount,
           t.averageCostBasisAmount,
           t.commissionAmount,
           t.denominator,
           t.payrollDate,
           t.purchaseDate,
           t.gainAmount,
           t.feesAmount,
           t.fractionalUnitsCashAccount,
           t.loadAmount,
           t.loanInterestAmount,
           t.loanPrincipalAmount,
           t.markdownAmount,
           t.markupAmount,
           t.newUnits,
           t.numerator,
           t.oldUnits,
           t.penaltyAmount,
           t.priorYearContribution,
           t.sharesPerContract,
           t.stateWithholding,
           t.totalAmount,
           t.taxesAmount,
           t.taxExempt,
           t.unitPrice,
           t.units,
           t.withholding,
           t.optionsSharesPerContract),
         t.securityInfo)}
    }

    implicit val investmentTransactionFormat: Format[InvestmentTransaction] =
      Format[InvestmentTransaction](investmentTransactionReads, investmentTransactionWrites)

    // =========================== TransactionsResponse Formats =================
    implicit val transactionsResponseFormat: Format[TransactionsResponse] = {
      val reads: Reads[TransactionsResponse] =
        ((__ \ "bankingTransactions").readNullable[Vector[BankingTransaction]] and
        (__ \ "creditCardTransactions").readNullable[Vector[CreditTransaction]] and
        (__ \ "investmentTransactions").readNullable[Vector[InvestmentTransaction]] and
        (__ \ "investmentBankingTransactions").readNullable[Vector[InvestmentBankingTransaction]] and
        (__ \ "loanTransactions").readNullable[Vector[LoanTransaction]] and
        (__ \ "rewardsTransactions").readNullable[Vector[RewardTransaction]] and
        (__ \ "notRefreshedReason").read[NotRefreshedReason])(
          (b, c, i, ib, l, r, nrr) => TransactionsResponse(Vector(b, c, i, ib, l, r).flatten.flatten, nrr)
        )
      val writes: Writes[TransactionsResponse] =
        ((__ \ "bankingTransactions").write[Vector[BankingTransaction]] and
        (__ \ "creditCardTransations").write[Vector[CreditTransaction]] and
        (__ \ "investmentTransactions").write[Vector[InvestmentTransaction]] and
        (__ \ "investmentBankingTransactions").write[Vector[InvestmentBankingTransaction]] and
        (__ \ "loanTransactions").write[Vector[LoanTransaction]] and
        (__ \ "rewardsTransactions").write[Vector[RewardTransaction]] and
        (__ \ "notRefreshedReason").write[NotRefreshedReason])( tr => {
        val b = tr.transactions collect {case x: BankingTransaction => x}
        val c = tr.transactions collect {case x: CreditTransaction => x}
        val i = tr.transactions collect {case x: InvestmentTransaction => x}
        val ib = tr.transactions collect {case x: InvestmentBankingTransaction => x}
        val l = tr.transactions collect {case x: LoanTransaction => x}
        val r = tr.transactions collect {case x: RewardTransaction => x}
        (b, c, i, ib, l, r, tr.notRefreshedReason)
      })
      Format(reads, writes)
    }

  }

}
