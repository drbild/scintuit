package scintuit.contrib.play

import com.github.nscala_money.money.Imports._
import com.github.nscala_money.money.json.PlayImports._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scintuit.data._
import scintuit.contrib.play.CommonFormats._

object InstitutionFormats extends InstitutionFormats

trait InstitutionFormats {

  implicit val keyFormat: Format[Key] = Format[Key](
    ((__ \ "name").read[String] and
      (__ \ "val").readNullable[String] and
      (__ \ "status").read[String] and
      (__ \ "valueLengthMin").readNullable[Int] and
      (__ \ "valueLengthMax").readNullable[Int] and
      (__ \ "displayFlag").read[Boolean] and
      (__ \ "displayOrder").read[Int] and
      (__ \ "mask").read[Boolean] and
      (__ \ "instructions").readNullable[String] and
      (__ \ "description").readNullable[String])(Key),
    ((__ \ "name").write[String] and
      (__ \ "val").writeNullable[String] and
      (__ \ "status").write[String] and
      (__ \ "valueLengthMin").writeNullable[Int] and
      (__ \ "valueLengthMax").writeNullable[Int] and
      (__ \ "displayFlag").write[Boolean] and
      (__ \ "displayOrder").write[Int] and
      (__ \ "mask").write[Boolean] and
      (__ \ "instructions").writeNullable[String] and
      (__ \ "description").writeNullable[String])(unlift(Key.unapply))
  )

  implicit val institutionSummaryFormat: Format[InstitutionSummary] = Format[InstitutionSummary](
    ((__ \ "institutionId").read[InstitutionId] and
      (__ \ "institutionName").read[String] and
      (__ \ "homeUrl").readNullable[String] and
      (__ \ "phoneNumber").readNullable[String] and
      (__ \ "virtual").readNullable[Boolean])(InstitutionSummary),
    ((__ \ "institutionId").write[InstitutionId] and
      (__ \ "institutionName").write[String] and
      (__ \ "homeUrl").writeNullable[String] and
      (__ \ "phoneNumber").writeNullable[String] and
      (__ \ "virtual").writeNullable[Boolean])(unlift(InstitutionSummary.unapply))
  )

  implicit val institutionFormat: Format[Institution] = Format[Institution](
    ((__ \ "institutionId").read[InstitutionId] and
      (__ \ "institutionName").read[String] and
      (__ \ "homeUrl").readNullable[String] and
      (__ \ "phoneNumber").readNullable[String] and
      (__ \ "virtual").readNullable[Boolean] and
      (__ \ "address").read[Address] and
      (__ \ "emailAddress").readNullable[String] and
      (__ \ "specialText").readNullable[String] and
      (__ \ "currencyCode").readNullable[CurrencyUnit] and
      (__ \ "keys").read[Seq[Key]])(Institution),
    ((__ \ "institutionId").write[InstitutionId] and
      (__ \ "institutionName").write[String] and
      (__ \ "homeUrl").writeNullable[String] and
      (__ \ "phoneNumber").writeNullable[String] and
      (__ \ "virtual").writeNullable[Boolean] and
      (__ \ "address").write[Address] and
      (__ \ "emailAddress").writeNullable[String] and
      (__ \ "specialText").writeNullable[String] and
      (__ \ "currencyCode").writeNullable[CurrencyUnit] and
      (__ \ "keys").write[Seq[Key]])(unlift(Institution.unapply))
  )
}
