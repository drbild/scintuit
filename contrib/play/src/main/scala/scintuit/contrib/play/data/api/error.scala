package scintuit.contrib.play.data.api

import play.api.libs.json._

import scintuit.data.api.error._
import scintuit.contrib.play.data.raw

object error {
  
  object ErrorFormats extends ErrorFormats
  
  trait ErrorFormats {
    import raw.error.{ErrorFormats => RawErrorFormats}

    implicit val errorTypeFormat: Format[ErrorType] = RawErrorFormats.errorTypeFormat
    implicit val errorCodeFormat: Format[ErrorCode] = RawErrorFormats.errorCodeFormat
    implicit val errorInfoFormat: Format[ErrorInfo] = RawErrorFormats.errorInfoFormat

  }

}
