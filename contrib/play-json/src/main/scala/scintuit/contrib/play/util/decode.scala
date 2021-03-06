package scintuit.contrib.play.util

import play.api.libs.json._
import scintuit.contrib.play.data.raw.all._
import scintuit.data.raw.account._
import scintuit.data.raw.error._
import scintuit.data.raw.institution._
import scintuit.data.raw.login._
import scintuit.data.raw.position._
import scintuit.data.raw.transaction._
import scintuit.util.parse.Decoder

import scalaz.\/
import scalaz.syntax.either._

object decode {

  object PlayDecoder extends PlayDecoder

  trait PlayDecoder extends Decoder {

    private val id: Format[JsValue] = Format[JsValue](Reads(JsSuccess(_)), Writes(identity))

    private def decode[A](body: String)(transform: Reads[JsValue])(implicit reads: Reads[A]): Exception \/ A =
      \/.fromTryCatchNonFatal(Json.parse(body))
        .leftMap { e => new Exception(s"Parse failure: Invalid json: ${body}", e) }
        .flatMap { j => (transform andThen reads).reads(j) match {
          case JsSuccess(a, _) => a.right
          case JsError(e) => new Exception(s"Parse failure: ${JsError.toJson(e).toString}").left
        }}

    override def errorInfo(body: String): Exception \/ ErrorInfo =
      decode[ErrorInfo](body)((__ \ "errorInfo")(0).json.pick)

    override def challenges(body: String): Exception \/ Vector[Challenge] =
      decode[Vector[Challenge]](body)((__ \ "challenge").json.pick andThen jsMap((__ \ "textOrImageAndChoice").json.pick))

    override def institution(body: String): Exception \/ InstitutionDetails =
      decode[InstitutionDetails](body)((__ \ "keys").json.update((__ \ "key").json.pick).map(identity))

    override def institutions(body: String): Exception \/ Vector[Institution] =
      decode[Vector[Institution]](body)((__ \ "institution").json.pick)

    override def account(body: String): Exception \/ Account =
      decode[Account](body)((__ \ "accounts")(0).json.pick)

    override def accounts(body: String): Exception \/ Vector[Account] =
      decode[Vector[Account]](body)((__ \ "accounts").json.pick)

    override def positions(body: String): Exception \/ Vector[Position] =
      decode[Vector[Position]](body)((__ \ "position").json.pick)

    override def transactions(body: String): Exception \/ TransactionsResponse =
      decode[TransactionsResponse](body)(id)

    // Utility functions
    private def traverse[A <: JsValue](as: JsArray)(f: JsValue => JsResult[A]): JsResult[JsArray] = as match {
      case JsArray(Nil) => JsSuccess(JsArray(Nil))
      case JsArray(h +: t) => f(h).flatMap(fh => traverse(JsArray(t))(f).map(fh +: _))
    }

    private def jsMap[A <: JsValue](f: Reads[A]): Reads[JsValue] = Reads {
      case as: JsArray => traverse(as)(f.reads)
      case _ => JsError("error.expected.array")
    }
  }

}
