package scintuit.contrib.play

import enumeratum.EnumFormats
import play.api.libs.json._
import scintuit.data._

object CommonFormats extends CommonFormats

trait CommonFormats {

  implicit val addressFormat: Format[Address] = Json.format[Address]

  implicit val notRefreshedReasonFormat: Format[NotRefreshedReason] = EnumFormats.formats(NotRefreshedReason, false)

  implicit val errorTypeFormat: Format[ErrorType] = EnumFormats.formats(ErrorType, false)
  implicit val errorCodeFormat: Format[ErrorCode] =
    Format(__.read[String].map(ErrorCode(_)), Writes(ec => JsString(ec.code)))
  implicit val errorInfoFormat: Format[ErrorInfo] = Json.format[ErrorInfo]
  implicit val statusFormat: Format[Status] = Json.format[Status]

}
