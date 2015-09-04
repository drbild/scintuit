package scintuit.data

import com.github.nscala_money.money.Imports._

case class Key(
  name: String,
  value: Option[String],
  status: String,
  valueLengthMin: Option[Int],
  valueLengthMax: Option[Int],
  displayFlag: Boolean,
  displayOrder: Int,
  mask: Boolean,
  instructions: Option[String],
  description: Option[String]
)

case class InstitutionSummary(
  id: InstitutionId,
  name: String,
  homeUrl: Option[String],
  phoneNumber: Option[String],
  virtual: Option[Boolean]
)

case class Institution (
  id: InstitutionId,
  name: String,
  homeUrl: Option[String],
  phoneNumber: Option[String],
  virtual: Option[Boolean],
  address: Address,
  emailAddress: Option[String],
  specialText: Option[String],
  currencyCode: Option[CurrencyUnit],
  keys: Seq[Key]
)
