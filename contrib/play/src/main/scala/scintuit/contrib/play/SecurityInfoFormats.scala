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
import org.cvogt.play.json.Jsonx
import org.cvogt.play.json.implicits.optionWithNull
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scintuit.data._

object SecurityInfoFormats extends SecurityInfoFormats

trait SecurityInfoFormats {
  private implicit val debtSecurityInfoFormat: Format[RawDebtSecurityInfo] = Jsonx.formatCaseClass[RawDebtSecurityInfo]
  private implicit val mutualFundSecurityInfoFormat: Format[RawMutualFundSecurityInfo] = Jsonx.formatCaseClass[RawMutualFundSecurityInfo]
  private implicit val stockSecurityInfoFormat: Format[RawStockSecurityInfo] = Jsonx.formatCaseClass[RawStockSecurityInfo]
  private implicit val otherSecurityInfoFormat: Format[RawOtherSecurityInfo] = Jsonx.formatCaseClass[RawOtherSecurityInfo]
  private implicit val optionSecurityInfoFormat: Format[RawOptionSecurityInfo] = Jsonx.formatCaseClass[RawOptionSecurityInfo]

  private case class DebtSI(debtInfo: RawDebtSecurityInfo)
  private case class MfSI(mfInfo: RawMutualFundSecurityInfo)
  private case class StockSI(stockInfo: RawStockSecurityInfo)
  private case class OptionSI(optionInfo: RawOptionSecurityInfo)
  private case class OtherSI(otherInfo: RawOtherSecurityInfo)

  private val rawSecurityInfoReads: Reads[Option[RawSecurityInfo]] =
    (taggedReads("DEBTINFO", Jsonx.formatCaseClass[DebtSI]).map(_.debtInfo: RawSecurityInfo) orElse
     taggedReads("MFINFO", Jsonx.formatCaseClass[MfSI]).map(_.mfInfo) orElse
     taggedReads("STOCKINFO", Jsonx.formatCaseClass[StockSI]).map(_.stockInfo) orElse
     taggedReads("OPTINFO", Jsonx.formatCaseClass[OptionSI]).map(_.optionInfo) orElse
     taggedReads("OTHERINFO", Jsonx.formatCaseClass[OtherSI]).map(_.otherInfo)).map(Option(_)) orElse
     Reads.pure(None)

  private val rawSecurityInfoWrites: Writes[Option[RawSecurityInfo]] =
    Writes[Option[RawSecurityInfo]](_ match {
      case Some(a) => a match {
        case debt: RawDebtSecurityInfo => taggedWrites("DEBTINFO", Jsonx.formatCaseClass[DebtSI]).writes(DebtSI(debt))
        case mf: RawMutualFundSecurityInfo => taggedWrites("MFINFO", Jsonx.formatCaseClass[MfSI]).writes(MfSI(mf))
        case stock: RawStockSecurityInfo => taggedWrites("STOCKINFO", Jsonx.formatCaseClass[StockSI]).writes(StockSI(stock))
        case option: RawOptionSecurityInfo => taggedWrites("OPTINFO", Jsonx.formatCaseClass[OptionSI]).writes(OptionSI(option))
        case other: RawOtherSecurityInfo => taggedWrites("OTHERINFO", Jsonx.formatCaseClass[OtherSI]).writes(OtherSI(other))
      }
      case None => Json.obj()
    })

  implicit val rawSecurityInfoFormat: Format[Option[RawSecurityInfo]] =
    Format[Option[RawSecurityInfo]](rawSecurityInfoReads, rawSecurityInfoWrites)

  // ================================ Helper Methods ================================
  private def taggedReads[A](tag: String, reads: Reads[A]): Reads[A] =
    (__ \ "invSecurityType").read[String].filter(_ == tag) andKeep reads

  private def taggedWrites[A](tag: String, writes: Writes[A]): Writes[A] =
    writes.transform(_.as[JsObject] +("invSecurityType", JsString(tag)))
}
