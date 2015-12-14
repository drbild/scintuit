package scintuit.contrib.play.util

import play.api.libs.json._
import scintuit.contrib.play.data.raw.all._
import scintuit.data.raw.account._
import scintuit.data.raw.login._
import scintuit.util.prepare.Encoder

object encode {

  object PlayEncoder extends PlayEncoder

  trait PlayEncoder extends Encoder {

    private def encode[A](a: A)(transformer: Writes[JsValue])(implicit writes: Writes[A]): String =
      (writes transform transformer).writes(a).toString

    override def credentials(credentials: Seq[Credentials]): String =
      encode(credentials)((__ \ "credentials" \ "credential").write)

    override def answers(answers: Seq[ChallengeAnswer]): String =
      encode(answers)((__ \ "challengeResponses" \ "response").write)

    override def accountType(typ: AccountType): String = {
      val label = typ match {
        case _: BankingAccountType => "banking"
        case _: CreditAccountType => "credit"
        case _: LoanAccountType => "loan"
        case _: InvestmentAccountType => "investment"
        case _: RewardAccountType => "rewards"
      }

      Json.obj(
        "type" -> s"${label}Account",
        s"${label}AccountType" -> Json.toJson(typ)
      ).toString
    }
  }

}
