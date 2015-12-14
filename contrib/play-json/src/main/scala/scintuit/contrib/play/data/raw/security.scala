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

import com.github.nscala_money.money.json.PlayImports._
import org.cvogt.play.json.Jsonx
import org.cvogt.play.json.implicits.optionWithNull
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scintuit.data.raw.security._

object security {
  
  object SecurityFormats extends SecurityFormats

  trait SecurityFormats {
    private implicit val debtSecurityInfoFormat: Format[DebtSecurityInfo] = Jsonx.formatCaseClass[DebtSecurityInfo]
    private implicit val mutualFundSecurityInfoFormat: Format[MutualFundSecurityInfo] = Jsonx.formatCaseClass[MutualFundSecurityInfo]
    private implicit val stockSecurityInfoFormat: Format[StockSecurityInfo] = Jsonx.formatCaseClass[StockSecurityInfo]
    private implicit val otherSecurityInfoFormat: Format[OtherSecurityInfo] = Jsonx.formatCaseClass[OtherSecurityInfo]
    private implicit val optionSecurityInfoFormat: Format[OptionSecurityInfo] = Jsonx.formatCaseClass[OptionSecurityInfo]

    private case class DebtSI(debtInfo: DebtSecurityInfo)
    private case class MfSI(mfInfo: MutualFundSecurityInfo)
    private case class StockSI(stockInfo: StockSecurityInfo)
    private case class OptionSI(optionInfo: OptionSecurityInfo)
    private case class OtherSI(otherInfo: OtherSecurityInfo)

    private val securityInfoReads: Reads[Option[SecurityInfo]] =
      (taggedReads("DEBTINFO", Jsonx.formatCaseClass[DebtSI]).map(_.debtInfo: SecurityInfo) orElse
        taggedReads("MFINFO", Jsonx.formatCaseClass[MfSI]).map(_.mfInfo) orElse
        taggedReads("STOCKINFO", Jsonx.formatCaseClass[StockSI]).map(_.stockInfo) orElse
        taggedReads("OPTINFO", Jsonx.formatCaseClass[OptionSI]).map(_.optionInfo) orElse
        taggedReads("OTHERINFO", Jsonx.formatCaseClass[OtherSI]).map(_.otherInfo)).map(Option(_)) orElse
        Reads.pure(None)

    private val securityInfoWrites: Writes[Option[SecurityInfo]] =
      Writes[Option[SecurityInfo]](_ match {
      case Some(a) => a match {
        case si: DebtSecurityInfo => taggedWrites("DEBTINFO", Jsonx.formatCaseClass[DebtSI]).writes(DebtSI(si))
        case si: MutualFundSecurityInfo => taggedWrites("MFINFO", Jsonx.formatCaseClass[MfSI]).writes(MfSI(si))
        case si: StockSecurityInfo => taggedWrites("STOCKINFO", Jsonx.formatCaseClass[StockSI]).writes(StockSI(si))
        case si: OptionSecurityInfo => taggedWrites("OPTINFO", Jsonx.formatCaseClass[OptionSI]).writes(OptionSI(si))
        case si: OtherSecurityInfo => taggedWrites("OTHERINFO", Jsonx.formatCaseClass[OtherSI]).writes(OtherSI(si))
      }
      case None => Json.obj()
    })

    implicit val securityInfoFormat: Format[Option[SecurityInfo]] =
      Format[Option[SecurityInfo]](securityInfoReads, securityInfoWrites)

    // ================================ Helper Methods ================================
    private def taggedReads[A](tag: String, reads: Reads[A]): Reads[A] =
      (__ \ "invSecurityType").read[String].filter(_ == tag) andKeep reads

    private def taggedWrites[A](tag: String, writes: Writes[A]): Writes[A] =
      writes.transform(_.as[JsObject] +("invSecurityType", JsString(tag)))
  }

}
