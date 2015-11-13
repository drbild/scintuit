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

object AccountFormats extends AccountFormats

trait AccountFormats {

  implicit val accountStatusFormat: Format[AccountStatus] = EnumFormats.formats(AccountStatus, false)
  
  private object AccountTypeFormats {
    val bankingAccountTypeFormat: Format[BankingAccountType] = EnumFormats.formats(BankingAccountType, false)
    val creditAccountTypeFormat: Format[CreditAccountType] = EnumFormats.formats(CreditAccountType, false)
    val investmentAccountTypeFormat: Format[InvestmentAccountType] = EnumFormats.formats(InvestmentAccountType, false)
    val loanAccountTypeFormat: Format[LoanAccountType] = EnumFormats.formats(LoanAccountType, false)
    val rewardAccountTypeFormat: Format[RewardAccountType] = EnumFormats.formats(RewardAccountType, false)
  }

  implicit val bankingAccountTypeReads: Reads[BankingAccountType] = AccountTypeFormats.bankingAccountTypeFormat
  implicit val creditAccountTypeReads: Reads[CreditAccountType] = AccountTypeFormats.creditAccountTypeFormat
  implicit val investmentAccountTypeReads: Reads[InvestmentAccountType] = AccountTypeFormats.investmentAccountTypeFormat
  implicit val loanAccountTypeReads: Reads[LoanAccountType] = AccountTypeFormats.loanAccountTypeFormat
  implicit val rewardAccountTypeReads: Reads[RewardAccountType] = AccountTypeFormats.rewardAccountTypeFormat
  
  implicit val accountTypeWrites: Writes[AccountType] = Writes {
    case t: BankingAccountType => AccountTypeFormats.bankingAccountTypeFormat.writes(t)
    case t: CreditAccountType => AccountTypeFormats.creditAccountTypeFormat.writes(t)
    case t: InvestmentAccountType => AccountTypeFormats.investmentAccountTypeFormat.writes(t)
    case t: LoanAccountType => AccountTypeFormats.loanAccountTypeFormat.writes(t)
    case t: RewardAccountType => AccountTypeFormats.rewardAccountTypeFormat.writes(t)
  }

  implicit val rateTypeFormat: Format[RateType] = EnumFormats.formats(RateType, false)
  implicit val paymentTypeFormat: Format[PaymentType] = EnumFormats.formats(PaymentType, false)
  implicit val loanTermTypeFormat: Format[LoanTermType] = EnumFormats.formats(LoanTermType, false)

  // =========================== Specific Account Formats ============================
  implicit val otherAccountFormat: Format[OtherAccount] = {
    val tagged = taggedFormat("otherAccount", Jsonx.formatCaseClass[RawOtherAccount])
    xmap(tagged)(OtherAccount, _.raw)
  }

  implicit val bankingAccountFormat: Format[BankingAccount] = {
    val tagged = taggedFormat("bankingAccount", Jsonx.formatCaseClass[RawBankingAccount])
    xmap(tagged)(BankingAccount, _.raw)
  }

  implicit val creditAccountFormat: Format[CreditAccount] = {
    val tagged = taggedFormat("creditAccount", Jsonx.formatCaseClass[RawCreditAccount])
    xmap(tagged)(CreditAccount, _.raw)
  }

  implicit val loanAccountFormat: Format[LoanAccount] = {
    val tagged = taggedFormat("loanAccount", Jsonx.formatCaseClass[RawLoanAccount])
    xmap(tagged)(LoanAccount, _.raw)
  }

  implicit val investmentAccountFormat: Format[InvestmentAccount] = {
    val tagged = taggedFormat("investmentAccount", Jsonx.formatCaseClass[RawInvestmentAccount])
    xmap(tagged)(InvestmentAccount, _.raw)
  }

  implicit val rewardAccountFormat: Format[RewardAccount] = {
    val tagged = taggedFormat("rewardAccount", Jsonx.formatCaseClass[RawRewardAccount])
    xmap(tagged)(RewardAccount, _.raw)
  }

  // ==================================== Account =====================================
  implicit val accountFormat: Format[Account] = Format[Account](
    otherAccountFormat.map(x => x: Account) orElse
    bankingAccountFormat.map(x => x: Account) orElse
    creditAccountFormat.map(x => x: Account) orElse
    investmentAccountFormat.map(x => x: Account) orElse
    loanAccountFormat.map(x => x: Account) orElse
    rewardAccountFormat.map(x => x: Account),
      Writes[Account](_ match {
      case other: OtherAccount => otherAccountFormat.writes(other)
      case banking: BankingAccount => bankingAccountFormat.writes(banking)
      case credit: CreditAccount => creditAccountFormat.writes(credit)
      case investment: InvestmentAccount => investmentAccountFormat.writes(investment)
      case loan: LoanAccount => loanAccountFormat.writes(loan)
      case reward: RewardAccount => rewardAccountFormat.writes(reward)
    })
  )

  // ================================ Helper Methods ================================
  private def taggedReads[A](tag: String, reads: Reads[A]): Reads[A] =
    (__ \ "type").read[String].filter(_ == tag) andKeep reads

  private def taggedWrites[A](tag: String, writes: Writes[A]): Writes[A] =
    writes.transform(_.as[JsObject] +("type", JsString(tag)))

  private def taggedFormat[A](tag: String, format: Format[A]): Format[A] =
    Format(taggedReads(tag, format), taggedWrites(tag, format))

  private def xmap[A, B](format: Format[A])(fab: A => B, fba: B => A): Format[B] =
    Format(format map fab, Writes(b => format.writes(fba(b))))

}
