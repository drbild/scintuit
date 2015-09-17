package scintuit.contrib.play

import com.github.nscala_money.money.json.PlayImplicits._
import org.cvogt.play.json.Jsonx
import org.cvogt.play.json.implicits.optionWithNull
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scintuit.data._

object PositionFormats extends PositionFormats

trait PositionFormats {

  private def positionReads: Reads[Position] = {
    val t: Reads[RawPosition] = Jsonx.formatCaseClass[RawPosition]
    val si: Reads[Option[RawSecurityInfo]] = SecurityInfoFormats.rawSecurityInfoFormat
    (t and si)(Position)
  }

  private def positionWrites: Writes[Position] = {
    val t: Writes[RawPosition] = Jsonx.formatCaseClass[RawPosition]
    val si: Writes[Option[RawSecurityInfo]] = SecurityInfoFormats.rawSecurityInfoFormat
    (__.write(t) and __.write(si))(unlift(Position.unapply))
  }

  implicit val PositionFormat: Format[Position] =
    Format[Position](positionReads, positionWrites)
}
