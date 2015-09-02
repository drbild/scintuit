package scintuit.data

case class Address(
  line1: Option[String],
  line2: Option[String],
  line3: Option[String],
  city: Option[String],
  state: Option[String],
  postalCode: Option[String],
  country: Option[String]
)
