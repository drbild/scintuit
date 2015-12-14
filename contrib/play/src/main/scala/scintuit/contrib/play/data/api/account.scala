package scintuit.contrib.play.data.api

import play.api.libs.json._

import scintuit.data.api.account._
import scintuit.contrib.play.data.raw

object account {

  object AccountFormats extends AccountFormats

  trait AccountFormats {
    import raw.account.{AccountFormats => RawAccountFormats}

    implicit val accountStatusFormat: Format[AccountStatus] = RawAccountFormats.accountStatusFormat

    implicit val rateTypeFormat: Format[RateType] = RawAccountFormats.rateTypeFormat
    implicit val paymentTypeFormat: Format[PaymentType] = RawAccountFormats.paymentTypeFormat
    implicit val loanTermTypeFormat: Format[LoanTermType] = RawAccountFormats.loanTermTypeFormat

    implicit val bankingAccountTypeReads: Reads[BankingAccountType] = RawAccountFormats.bankingAccountTypeReads
    implicit val creditAccountTypeReads: Reads[CreditAccountType] = RawAccountFormats.creditAccountTypeReads
    implicit val investmentAccountTypeReads: Reads[InvestmentAccountType] = RawAccountFormats.investmentAccountTypeReads
    implicit val loanAccountTypeReads: Reads[LoanAccountType] = RawAccountFormats.loanAccountTypeReads
    implicit val rewardAccountTypeReads: Reads[RewardAccountType] = RawAccountFormats.rewardAccountTypeReads

    implicit val accountTypeWrites: Writes[AccountType] = RawAccountFormats.accountTypeWrites

    implicit val accountFormat: Format[Account] =
      xmap(raw.account.AccountFormats.accountFormat)(Account.apply, _.raw)

    private def xmap[A, B](format: Format[A])(fab: A => B, fba: B => A): Format[B] =
      Format(format map fab, Writes(b => format.writes(fba(b))))

  }

}
