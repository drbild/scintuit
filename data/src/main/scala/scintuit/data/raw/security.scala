package scintuit.data.raw

import com.github.nscala_money.money.Imports._
import com.github.nscala_time.time.Imports._

/**
 * Module for security info resources
 */
object security {

  sealed trait SecurityInfo {
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

  case class DebtSecurityInfo(
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
  ) extends SecurityInfo

  case class MutualFundSecurityInfo(
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
  ) extends SecurityInfo

  case class OptionSecurityInfo(
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
  ) extends SecurityInfo

  case class StockSecurityInfo(
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
  ) extends SecurityInfo

  case class OtherSecurityInfo(
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
  ) extends SecurityInfo

}
