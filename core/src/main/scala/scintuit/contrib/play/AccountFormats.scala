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
  implicit val bankingAccountTypeFormat: Format[BankingAccountType] = EnumFormats.formats(BankingAccountType, false)
  implicit val creditAccountTypeFormat: Format[CreditAccountType] = EnumFormats.formats(CreditAccountType, false)

  // =========================== Specific Account Formats ============================
  implicit val bankingAccountFormat: Format[BankingAccount] = {
    val tagged = taggedFormat("bankingAccount", Jsonx.formatCaseClass[RawBankingAccount])
    xmap(tagged)(BankingAccount, _.raw)
  }

  implicit val creditAccountFormat: Format[CreditAccount] = {
    val tagged = taggedFormat("creditAccount", Jsonx.formatCaseClass[RawCreditAccount])
    xmap(tagged)(CreditAccount, _.raw)
  }

  // ==================================== Account =====================================
  implicit val accountFormat: Format[Account] = Format[Account](
    bankingAccountFormat.map(x => x: Account) orElse
      creditAccountFormat.map(x => x: Account) orElse
      Reads(_ => JsSuccess(BankingAccount(null))),
    Writes[Account](_ match {
      case banking: BankingAccount => bankingAccountFormat.writes(banking)
      case credit: CreditAccount => creditAccountFormat.writes(credit)
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
