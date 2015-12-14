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
import scintuit.data.raw

import scalaz.std.option._
import scalaz.syntax.apply._

/**
 * Module for security resources
 */
object security {

  type RawSecurityInfo = raw.security.SecurityInfo
  type RawDebtSecurityInfo = raw.security.DebtSecurityInfo
  type RawMutualFundSecurityInfo = raw.security.MutualFundSecurityInfo
  type RawOptionSecurityInfo = raw.security.OptionSecurityInfo
  type RawStockSecurityInfo = raw.security.StockSecurityInfo
  type RawOtherSecurityInfo = raw.security.OtherSecurityInfo

  val RawDebtSecurityInfo = raw.security.DebtSecurityInfo
  val RawMutualFundSecurityInfo = raw.security.MutualFundSecurityInfo
  val RawOptionSecurityInfo = raw.security.OptionSecurityInfo
  val RawStockSecurityInfo = raw.security.StockSecurityInfo
  val RawOtherSecurityInfo = raw.security.OtherSecurityInfo

  object Security {
    def fromRaw(raw: RawSecurityInfo): Security = raw match {
      case r: RawDebtSecurityInfo => DebtSecurity(r)
      case r: RawMutualFundSecurityInfo => MutualFundSecurity(r)
      case r: RawOptionSecurityInfo => OptionSecurity(r)
      case r: RawStockSecurityInfo => StockSecurity(r)
      case r: RawOtherSecurityInfo => OtherSecurity(r)
    }
  }

  sealed trait Security {
    val raw: RawSecurityInfo

    private[data] def toMoney(amount: Option[BigDecimal]): Option[BigMoney] =
      (raw.currencyCode |@| amount)(BigMoney.of)

    def memo: Option[String] = raw.memo
    def currency: Option[CurrencyUnit] = raw.currencyCode
    def currencyRate: Option[BigDecimal] = raw.currencyRate

    def idUnique: Option[String] = raw.uniqueId
    def idUniqueType: Option[String] = raw.uniqueIdType
    def idInstitution: Option[String] = raw.fiId

    def ticker: Option[String] = raw.ticker
    def name: Option[String] = raw.name
    def fund: Option[String] = raw.fundName
    def rating: Option[String] = raw.rating
    def symbolReferenceId: Option[String] = raw.symbolRefId

    def assetClass: Option[String] = raw.assetClass
    def assetClassInstitution: Option[String] = raw.fiAssetClass

    def unitPrice: Option[BigMoney] = toMoney(raw.unitPrice)
    def unitPriceDate: Option[DateTime] = raw.asOfDate
  }

  case class DebtSecurity(raw: RawDebtSecurityInfo) extends Security {
    def callDate: Option[DateTime] = raw.callDate
    def callPrice: Option[BigMoney] = toMoney(raw.callPrice)
    def callType: Option[String] = raw.callType

    def couponFrequency: Option[String] = raw.couponFreq
    def couponMaturityDate: Option[DateTime] = raw.couponMaturityDate
    def couponRate: Option[BigDecimal] = raw.couponRate

    def debtClass: Option[String] = raw.debtClass
    def debtType: Option[String] = raw.debtType

    def maturityDate: Option[DateTime] = raw.maturityDate
    def parValue: Option[BigMoney] = toMoney(raw.parValue)

    def yieldToMaturity: Option[BigDecimal] = raw.yieldToMaturity
    def yieldToCall: Option[BigDecimal] = raw.yieldToCall
  }

  case class MutualFundSecurity(raw: RawMutualFundSecurityInfo) extends Security {
    def fundType: Option[String] = raw.mfType
    def fundManager: Option[String] = raw.fundManager
    def `yield`: Option[BigDecimal] = raw.`yield`
    def yieldDate: Option[DateTime] = raw.yieldAsOfDate
  }

  case class OptionSecurity(raw: RawOptionSecurityInfo) extends Security {
    def optionType: Option[String] = raw.optType

    def strikePrice: Option[BigMoney] = toMoney(raw.strikePrice)
    def expiration: Option[DateTime] = raw.expireDate
    def sharesPerContract: Option[Long] = raw.sharesPerContract

    def securityUniqueId: Option[String] = raw.securityUniqueId
    def securityUniqueIdType: Option[String] = raw.securityUniqueIdType
  }

  case class StockSecurity(raw: RawStockSecurityInfo) extends Security {
    def stockType: Option[String] = raw.stockType
    def `yield`: Option[BigDecimal] = raw.`yield`
    def yieldDate: Option[DateTime] = raw.yieldAsOfDate
  }

  case class OtherSecurity(raw: RawOtherSecurityInfo) extends Security {
    def otherType: Option[String] = raw.typeDesc
  }

}
