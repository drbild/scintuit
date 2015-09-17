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

package scintuit.data

import com.github.nscala_money.money.Imports._
import com.github.nscala_time.time.Imports._

import scalaz.Scalaz._


// ================================ SecurityInfo Types ================================
sealed trait RawSecurityInfo {
  val assetClass: Option[String]
  val fiAssetClass: Option[String]
  val ticker: Option[String]
  val uniqueId: Option[String]
  val uniqueIdType: Option[String]
  val asOfDate: Option[DateTime]
  val rating: Option[String]
  val fiId: Option[String]
  val name: Option[String]
  val fundName: Option[String]
  val memo: Option[String]
  val symbolRefId: Option[String]
  val currencyCode: Option[CurrencyUnit]
  val currencyRate: Option[BigDecimal]
  val unitPrice: Option[BigDecimal]
}

object SecurityInfo {
  def fromRaw(raw: RawSecurityInfo): SecurityInfo = raw match {
    case r: RawDebtSecurityInfo => DebtSecurityInfo(r)
    case r: RawMutualFundSecurityInfo => MutualFundSecurityInfo(r)
    case r: RawStockSecurityInfo => StockSecurityInfo(r)
    case r: RawOptionSecurityInfo => OptionSecurityInfo(r)
    case r: RawOtherSecurityInfo => OtherSecurityInfo(r)
  }
}

sealed trait SecurityInfo {
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

final case class RawDebtSecurityInfo(
  assetClass: Option[String],
  fiAssetClass: Option[String],
  ticker: Option[String],
  uniqueId: Option[String],
  uniqueIdType: Option[String],
  asOfDate: Option[DateTime],
  rating: Option[String],
  percent: Option[BigDecimal],
  fiId: Option[String],
  name: Option[String],
  fundName: Option[String],
  memo: Option[String],
  symbolRefId: Option[String],
  currencyCode: Option[CurrencyUnit],
  currencyRate: Option[BigDecimal],
  unitPrice: Option[BigDecimal],
  callDate: Option[DateTime],
  yieldToCall: Option[BigDecimal],
  callPrice: Option[BigDecimal],
  callType: Option[String],
  couponFreq: Option[String],
  couponMaturityDate: Option[DateTime],
  couponRate: Option[BigDecimal],
  debtClass: Option[String],
  debtType: Option[String],
  maturityDate: Option[DateTime],
  yieldToMaturity: Option[BigDecimal],
  parValue: Option[BigDecimal]
) extends RawSecurityInfo

final case class DebtSecurityInfo(raw: RawDebtSecurityInfo) extends SecurityInfo {
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

case class RawMutualFundSecurityInfo(
  assetClass: Option[String],
  fiAssetClass: Option[String],
  ticker: Option[String],
  uniqueId: Option[String],
  uniqueIdType: Option[String],
  asOfDate: Option[DateTime],
  rating: Option[String],
  percent: Option[BigDecimal],
  fiId: Option[String],
  name: Option[String],
  fundName: Option[String],
  memo: Option[String],
  symbolRefId: Option[String],
  currencyCode: Option[CurrencyUnit],
  currencyRate: Option[BigDecimal],
  unitPrice: Option[BigDecimal],
  mfType: Option[String],
  fundManager: Option[String],
  `yield`: Option[BigDecimal],
  yieldAsOfDate: Option[DateTime]
) extends RawSecurityInfo

final case class MutualFundSecurityInfo(raw: RawMutualFundSecurityInfo) extends SecurityInfo {
  def fundType: Option[String] = raw.mfType
  def fundManager: Option[String] = raw.fundManager
  def `yield`: Option[BigDecimal] = raw.`yield`
  def yieldDate: Option[DateTime] = raw.yieldAsOfDate
}

case class RawStockSecurityInfo(
  assetClass: Option[String],
  fiAssetClass: Option[String],
  ticker: Option[String],
  uniqueId: Option[String],
  uniqueIdType: Option[String],
  asOfDate: Option[DateTime],
  rating: Option[String],
  percent: Option[BigDecimal],
  fiId: Option[String],
  name: Option[String],
  fundName: Option[String],
  memo: Option[String],
  symbolRefId: Option[String],
  currencyCode: Option[CurrencyUnit],
  currencyRate: Option[BigDecimal],
  unitPrice: Option[BigDecimal],
  stockType: Option[String],
  `yield`: Option[BigDecimal],
  yieldAsOfDate: Option[DateTime]
) extends RawSecurityInfo

final case class StockSecurityInfo(raw: RawStockSecurityInfo) extends SecurityInfo {
  def stockType: Option[String] = raw.stockType
  def `yield`: Option[BigDecimal] = raw.`yield`
  def yieldDate: Option[DateTime] = raw.yieldAsOfDate
}

case class RawOtherSecurityInfo(
  assetClass: Option[String],
  fiAssetClass: Option[String],
  ticker: Option[String],
  uniqueId: Option[String],
  uniqueIdType: Option[String],
  asOfDate: Option[DateTime],
  rating: Option[String],
  percent: Option[BigDecimal],
  fiId: Option[String],
  name: Option[String],
  fundName: Option[String],
  memo: Option[String],
  symbolRefId: Option[String],
  currencyCode: Option[CurrencyUnit],
  currencyRate: Option[BigDecimal],
  unitPrice: Option[BigDecimal],
  typeDesc: Option[String]
) extends RawSecurityInfo

final case class OtherSecurityInfo(raw: RawOtherSecurityInfo) extends SecurityInfo {
  def otherType: Option[String] = raw.typeDesc
}

case class RawOptionSecurityInfo(
  assetClass: Option[String],
  fiAssetClass: Option[String],
  ticker: Option[String],
  uniqueId: Option[String],
  uniqueIdType: Option[String],
  asOfDate: Option[DateTime],
  rating: Option[String],
  percent: Option[BigDecimal],
  fiId: Option[String],
  name: Option[String],
  fundName: Option[String],
  memo: Option[String],
  symbolRefId: Option[String],
  currencyCode: Option[CurrencyUnit],
  currencyRate: Option[BigDecimal],
  unitPrice: Option[BigDecimal],
  expireDate: Option[DateTime],
  strikePrice: Option[BigDecimal],
  optType: Option[String],
  securityUniqueId: Option[String],
  securityUniqueIdType: Option[String],
  sharesPerContract: Option[Long]
) extends RawSecurityInfo

final case class OptionSecurityInfo(raw: RawOptionSecurityInfo) extends SecurityInfo {
  def optionType: Option[String] = raw.optType

  def strikePrice: Option[BigMoney] = toMoney(raw.strikePrice)
  def expiration: Option[DateTime] = raw.expireDate
  def sharesPerContract: Option[Long] = raw.sharesPerContract

  def securityUniqueId: Option[String] = raw.securityUniqueId
  def securityUniqueIdType: Option[String] = raw.securityUniqueIdType
}
