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
import org.cvogt.play.json.Jsonx
import org.cvogt.play.json.implicits.optionWithNull
import play.api.libs.functional.syntax._
import play.api.libs.json._

import scintuit.data.raw.position._
import scintuit.data.raw.security._

object position {

  object PositionFormats extends PositionFormats

  trait PositionFormats {
    import security.SecurityFormats

    private case class PartialPosition(
      investmentPositionId: PositionId,
      changePercent: Option[BigDecimal],
      costBasis: Option[BigDecimal],
      currencyCode: Option[CurrencyUnit],
      currencyRate: Option[BigDecimal],
      currencyType: Option[String],
      unitPrice: Option[BigDecimal],
      priceAsOfDate: Option[DateTime],
      cusip: Option[String],
      dailyChange: Option[BigDecimal],
      memo: Option[String],
      empPretaxContribAmount: Option[BigDecimal],
      empMatchAmount: Option[BigDecimal],
      heldInAccount: Option[String],
      holdType: Option[String],
      investmentAllocation: Option[String],
      investmentDirection: Option[String],
      paidPrice: Option[BigDecimal],
      marketValue: Option[BigDecimal],
      maturityValue: Option[BigDecimal],
      units: Option[BigDecimal],
      unitUserQuantity: Option[BigDecimal],
      unitStreetQuantity: Option[BigDecimal],
      positionType: Option[String],
      positionStatus: Option[String],
      secured: Option[String],
      inv401kSource: Option[String],
      reinvestmentCapGains: Option[Boolean],
      reinvestmentDividend: Option[Boolean],
      transactionType: Option[String]
    )

    private def positionReads: Reads[Position] = {
      val pp: Reads[PartialPosition] = (Jsonx.formatCaseClass[PartialPosition]: Reads[PartialPosition])
      val si: Reads[Option[SecurityInfo]] = SecurityFormats.securityInfoFormat
      (pp and si){(pp, si) => 
        Position(
          pp.investmentPositionId,
          pp.changePercent,
          pp.costBasis,
          pp.currencyCode,
          pp.currencyRate,
          pp.currencyType,
          pp.unitPrice,
          pp.priceAsOfDate,
          pp.cusip,
          pp.dailyChange,
          pp.memo,
          pp.empPretaxContribAmount,
          pp.empMatchAmount,
          pp.heldInAccount,
          pp.holdType,
          pp.investmentAllocation,
          pp.investmentDirection,
          pp.paidPrice,
          pp.marketValue,
          pp.maturityValue,
          pp.units,
          pp.unitUserQuantity,
          pp.unitStreetQuantity,
          pp.positionType,
          pp.positionStatus,
          pp.secured,
          pp.inv401kSource,
          pp.reinvestmentCapGains,
          pp.reinvestmentDividend,
          pp.transactionType,
          si
        )}
    }

    private def positionWrites: Writes[Position] = {
      val pp: Writes[PartialPosition] = Jsonx.formatCaseClass[PartialPosition]
      val si: Writes[Option[SecurityInfo]] = SecurityFormats.securityInfoFormat
      (__.write(pp) and __.write(si)){p =>
        (PartialPosition(
           p.investmentPositionId,
           p.changePercent,
           p.costBasis,
           p.currencyCode,
           p.currencyRate,
           p.currencyType,
           p.unitPrice,
           p.priceAsOfDate,
           p.cusip,
           p.dailyChange,
           p.memo,
           p.empPretaxContribAmount,
           p.empMatchAmount,
           p.heldInAccount,
           p.holdType,
           p.investmentAllocation,
           p.investmentDirection,
           p.paidPrice,
           p.marketValue,
           p.maturityValue,
           p.units,
           p.unitUserQuantity,
           p.unitStreetQuantity,
           p.positionType,
           p.positionStatus,
           p.secured,
           p.inv401kSource,
           p.reinvestmentCapGains,
           p.reinvestmentDividend,
           p.transactionType),
         p.securityInfo)}
    }

    implicit val positionFormat: Format[Position] =
      Format[Position](positionReads, positionWrites)
  }

}
