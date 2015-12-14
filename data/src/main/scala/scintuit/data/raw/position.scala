package scintuit.data.raw

import com.github.nscala_money.money.Imports._
import com.github.nscala_time.time.Imports._

import scintuit.data.raw.security._

/**
 * Module for position resources
 */
object position {

  type PositionId = Long

  case class Position(
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
    transactionType: Option[String],
    securityInfo: Option[SecurityInfo]
  )

}
