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

package scintuit.data.api

import com.github.nscala_money.money.Imports._
import com.github.nscala_time.time.Imports._

import scintuit.data.api.security._
import scintuit.data.raw

import scalaz.std.option._
import scalaz.syntax.apply._

/**
 * Module for position resources
 */
object position {

  type PositionId = raw.position.PositionId

  type RawPosition = raw.position.Position
  val RawPosition = raw.position.Position

  case class Position(raw: RawPosition) {
    private[data] def toMoney(amount: Option[BigDecimal]): Option[BigMoney] =
      (raw.currencyCode |@| amount)(BigMoney.of)

    def id: PositionId = raw.investmentPositionId
    def memo: Option[String] = raw.memo

    def cusip: Option[String] = raw.cusip
    def holdingType: Option[String] = raw.holdType
    def subaccount: Option[String] = raw.heldInAccount
    def secured: Option[String] = raw.secured
    def source401K: Option[String] = raw.inv401kSource
    def transactionType: Option[String] = raw.transactionType

    def reinvestCapitalGains: Option[Boolean] = raw.reinvestmentCapGains
    def reinvestDividends: Option[Boolean] = raw.reinvestmentDividend

    def currency: Option[CurrencyUnit] = raw.currencyCode
    def currencyRate: Option[BigDecimal] = raw.currencyRate
    def currencyType: Option[String] = raw.currencyType

    def units: Option[BigDecimal] = raw.units
    def unitsUser: Option[BigDecimal] = raw.unitUserQuantity
    def unitsInstitution: Option[BigDecimal] = raw.unitStreetQuantity
    def unitPrice: Option[BigMoney] = toMoney(raw.unitPrice)
    def unitPriceDate: Option[DateTime] = raw.priceAsOfDate

    def costBasis: Option[BigMoney] = toMoney(raw.costBasis)
    def pricePaid: Option[BigMoney] = toMoney(raw.paidPrice)
    def valueMarket: Option[BigMoney] = toMoney(raw.marketValue)
    def valueMaturity: Option[BigMoney] = toMoney(raw.maturityValue)

    def changeDaily: Option[BigMoney] = toMoney(raw.dailyChange)
    def changeDailyPercent: Option[BigDecimal] = raw.changePercent

    def employerMatch: Option[BigMoney] = toMoney(raw.empMatchAmount)
    def employerPretaxContribution: Option[BigMoney] = toMoney(raw.empPretaxContribAmount)

    def investmentAllocation: Option[String] = raw.investmentAllocation
    def investmentDirection: Option[String] = raw.investmentDirection

    def positionType: Option[String] = raw.positionType
    def positionStatus: Option[String] = raw.positionStatus

    def security: Option[Security] = raw.securityInfo map (Security.fromRaw)
  }

}
