package scintuit.data.raw

import com.github.nscala_money.money.Imports._

/**
 * Module for institution resources
 */
object institution {

  type InstitutionId = Long

  case class Address(
    address1: Option[String],
    address2: Option[String],
    address3: Option[String],
    city: Option[String],
    state: Option[String],
    postalCode: Option[String],
    country: Option[String]
  )

  case class Key(
    name: String,
    `val`: Option[String],
    status: String,
    valueLengthMin: Option[Int],
    valueLengthMax: Option[Int],
    displayFlag: Boolean,
    displayOrder: Int,
    mask: Boolean,
    instructions: Option[String],
    description: Option[String]
  )

  case class Institution(
    institutionId: InstitutionId,
    institutionName: String,
    homeUrl: Option[String],
    phoneNumber: Option[String],
    virtual: Option[Boolean]
  )

  case class InstitutionDetails (
    institutionId: InstitutionId,
    institutionName: String,
    homeUrl: Option[String],
    phoneNumber: Option[String],
    virtual: Option[Boolean],
    address: Address,
    emailAddress: Option[String],
    specialText: Option[String],
    currencyCode: Option[CurrencyUnit],
    keys: Vector[Key]
  )

}
