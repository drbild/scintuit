package scintuit.data

case class Address(
  address1: Option[String],
  address2: Option[String],
  address3: Option[String],
  city: Option[String],
  state: Option[String],
  postalCode: Option[String],
  country: Option[String]
)
