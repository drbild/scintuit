package scintuit.data

import com.github.nscala_money.money.Imports._
import com.github.nscala_time.time.Imports._

import scalaz.Scalaz._

// ================================ Position ================================
final case class RawPosition(
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

final case class Position(raw: RawPosition, rawInfo: Option[RawSecurityInfo]) {
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

  def securityInfo: Option[SecurityInfo] = rawInfo map (SecurityInfo.fromRaw)
}
