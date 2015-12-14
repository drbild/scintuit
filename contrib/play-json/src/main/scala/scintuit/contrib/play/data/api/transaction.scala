package scintuit.contrib.play.data.api

import play.api.libs.json._
import play.api.libs.functional.syntax._
import scintuit.data.api.transaction._
import scintuit.contrib.play.data.raw

object transaction {

  object TransactionFormats extends TransactionFormats

  trait TransactionFormats {

    import raw.transaction.{TransactionFormats => RawTransactionFormats}

    implicit val correctionActionFormat: Format[CorrectionAction] = RawTransactionFormats.correctionActionFormat

    implicit val investmentSubAccountTypeFormat: Format[InvestmentSubAccountType] = RawTransactionFormats.investmentSubAccountTypeFormat
    implicit val banking401KSourceTypeFormat: Format[Banking401KSourceType] = RawTransactionFormats.banking401KSourceTypeFormat

    implicit val buyTypeFormat: Format[BuyType] = RawTransactionFormats.buyTypeFormat
    implicit val incomeTypeFormat: Format[IncomeType] = RawTransactionFormats.incomeTypeFormat
    implicit val optionsActionFormat: Format[OptionsAction] = RawTransactionFormats.optionsActionFormat
    implicit val optionsBuyTypeFormat: Format[OptionsBuyType] = RawTransactionFormats.optionsBuyTypeFormat
    implicit val optionsSellTypeFormat: Format[OptionsSellType] = RawTransactionFormats.optionsSellTypeFormat
    implicit val positionTypeFormat: Format[PositionType] = RawTransactionFormats.positionTypeFormat
    implicit val transferTypeFormat: Format[TransferAction] = RawTransactionFormats.transferTypeFormat
    implicit val relatedOptionTransactionTypeFormat: Format[RelatedOptionTransactionType] = RawTransactionFormats.relatedOptionTransactionTypeFormat
    implicit val securedTypeFormat: Format[SecuredType] = RawTransactionFormats.securedTypeFormat
    implicit val sellReasonFormat: Format[SellReason] = RawTransactionFormats.sellReasonFormat
    implicit val sellTypeFormat: Format[SellType] = RawTransactionFormats.sellTypeFormat

    private val bankingTransactionFormat: Format[BankingTransaction] =
      xmap(taggedFormat("banking", RawTransactionFormats.bankingTransactionFormat))(BankingTransaction, _.raw)

    private val creditTransactionFormat: Format[CreditTransaction] =
      xmap(taggedFormat("credit", RawTransactionFormats.creditTransactionFormat))(CreditTransaction, _.raw)

    private val investmentTransactionFormat: Format[InvestmentTransaction] =
      xmap(taggedFormat("investment", RawTransactionFormats.investmentTransactionFormat))(InvestmentTransaction, _.raw)

    private val investmentBankingTransactionFormat: Format[InvestmentBankingTransaction] =
      xmap(taggedFormat("investment_banking", RawTransactionFormats.investmentBankingTransactionFormat))(InvestmentBankingTransaction, _.raw)

    private val loanTransactionFormat: Format[LoanTransaction] =
      xmap(taggedFormat("loan", RawTransactionFormats.loanTransactionFormat))(LoanTransaction, _.raw)

    private val rewardTransactionFormat: Format[RewardTransaction] =
      xmap(taggedFormat("reward", RawTransactionFormats.rewardTransactionFormat))(RewardTransaction, _.raw)

    implicit val transactionFormat: Format[Transaction] = Format[Transaction](
      bankingTransactionFormat.map(t => t: Transaction) orElse
      creditTransactionFormat.map(identity) orElse
      investmentTransactionFormat.map(identity) orElse
      investmentBankingTransactionFormat.map(identity) orElse
      loanTransactionFormat.map(identity) orElse
      rewardTransactionFormat.map(identity),
      Writes[Transaction]{
        case t: BankingTransaction => bankingTransactionFormat.writes(t)
        case t: CreditTransaction => creditTransactionFormat.writes(t)
        case t: InvestmentTransaction => investmentTransactionFormat.writes(t)
        case t: InvestmentBankingTransaction => investmentBankingTransactionFormat.writes(t)
        case t: LoanTransaction => loanTransactionFormat.writes(t)
        case t: RewardTransaction => rewardTransactionFormat.writes(t)
      }
    )

    private def taggedReads[A](tag: String, reads: Reads[A]): Reads[A] =
      (__ \ "tag").read[String].filter(_ == tag) andKeep reads

    private def taggedWrites[A](tag: String, writes: Writes[A]): Writes[A] =
      writes.transform(_.as[JsObject] +("tag", JsString(tag)))

    private def taggedFormat[A](tag: String, format: Format[A]): Format[A] =
      Format(taggedReads(tag, format), taggedWrites(tag, format))

    private def xmap[A, B](format: Format[A])(fab: A => B, fba: B => A): Format[B] =
      Format(format map fab, Writes(b => format.writes(fba(b))))

  }

}
