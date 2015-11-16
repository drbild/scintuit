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

package scintuit.contrib.play

import com.github.nscala_money.money.json.PlayImplicits._
import enumeratum.EnumFormats
import org.cvogt.play.json.Jsonx
import org.cvogt.play.json.implicits.optionWithNull
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scintuit.contrib.play.CommonFormats._
import scintuit.data._

object TransactionFormats extends TransactionFormats

trait TransactionFormats {

  implicit val CorrectionActionFormat: Format[CorrectionAction] = EnumFormats.formats(CorrectionAction, false)

  // =========================== Investment Banking Transaction Type Formats =================
  implicit val InvestmentSubAccountTypeFormat: Format[InvestmentSubAccountType] = EnumFormats.formats(InvestmentSubAccountType, false)
  implicit val Banking401KSourceTypeFormat: Format[Banking401KSourceType] = EnumFormats.formats(Banking401KSourceType, false)

  // =========================== Investment Transaction Type Formats =================
  implicit val BuyTypeFormat: Format[BuyType] = EnumFormats.formats(BuyType, false)
  implicit val IncomeTypeFormat: Format[IncomeType] = EnumFormats.formats(IncomeType, false)
  implicit val OptionsActionFormat: Format[OptionsAction] = EnumFormats.formats(OptionsAction, false)
  implicit val OptionsBuyTypeFormat: Format[OptionsBuyType] = EnumFormats.formats(OptionsBuyType, false)
  implicit val OptionsSellTypeFormat: Format[OptionsSellType] = EnumFormats.formats(OptionsSellType, false)
  implicit val PositionTypeFormat: Format[PositionType] = EnumFormats.formats(PositionType, false)
  implicit val TransferTypeFormat: Format[TransferAction] = EnumFormats.formats(TransferAction, false)
  implicit val RelatedOptionTransactionTypeFormat: Format[RelatedOptionTransactionType] = EnumFormats.formats(RelatedOptionTransactionType, false)
  implicit val SecuredTypeFormat: Format[SecuredType] = EnumFormats.formats(SecuredType, false)
  implicit val SellReasonFormat: Format[SellReason] = EnumFormats.formats(SellReason, false)
  implicit val SellTypeFormat: Format[SellType] = EnumFormats.formats(SellType, false)

  // =========================== Categorization Formats =================
  implicit val categorizationSourceFormat: Format[CategorizationSource] = EnumFormats.formats(CategorizationSource, false)

  implicit val rawCategorizationCommonFormat = Jsonx.formatCaseClass[RawCategorizationCommon]
  implicit val rawCategorizationContextFormat = Jsonx.formatCaseClass[RawCategorizationContext]
  implicit val rawCategorizationFormat = Jsonx.formatCaseClass[RawCategorization]

  // =========================== Specific Transaction Formats =================
  implicit val bankingTransactionFormat: Format[BankingTransaction] =
    xmap(Jsonx.formatCaseClass[RawBankingTransaction])(BankingTransaction, _.raw)

  implicit val creditTransactionFormat: Format[CreditTransaction] =
    xmap(Jsonx.formatCaseClass[RawCreditTransaction])(CreditTransaction, _.raw)

  implicit val loanTransactionFormat: Format[LoanTransaction] =
    xmap(Jsonx.formatCaseClass[RawLoanTransaction])(LoanTransaction, _.raw)

  implicit val rewardTransactionFormat: Format[RewardTransaction] =
    xmap(Jsonx.formatCaseClass[RawRewardTransaction])(RewardTransaction, _.raw)

  implicit val investmentBankingTransactionFormat: Format[InvestmentBankingTransaction] =
    xmap(Jsonx.formatCaseClass[RawInvestmentBankingTransaction])(InvestmentBankingTransaction, _.raw)

  private def investmentTransactionReads: Reads[InvestmentTransaction] = {
    val t: Reads[RawInvestmentTransaction] = Jsonx.formatCaseClass[RawInvestmentTransaction]
    val si: Reads[Option[RawSecurityInfo]] = SecurityInfoFormats.rawSecurityInfoFormat
    (t and si)(InvestmentTransaction)
  }

  private def investmentTransactionWrites: Writes[InvestmentTransaction] = {
    val t: Writes[RawInvestmentTransaction] = Jsonx.formatCaseClass[RawInvestmentTransaction]
    val si: Writes[Option[RawSecurityInfo]] = SecurityInfoFormats.rawSecurityInfoFormat
    (__.write(t) and __.write(si))(unlift(InvestmentTransaction.unapply))
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

  // ================================ Helper Methods ================================
  private def xmap[A, B](format: Format[A])(fab: A => B, fba: B => A): Format[B] =
    Format(format map fab, Writes(b => format.writes(fba(b))))
}
