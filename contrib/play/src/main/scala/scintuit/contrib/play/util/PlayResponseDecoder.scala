package scintuit.contrib.play.util

import play.api.libs.json._
import scintuit.contrib.play.all._
import scintuit.data._
import scintuit.util.response.ResponseDecoder

import scalaz.\/
import scalaz.syntax.either._

object PlayResponseDecoder extends PlayResponseDecoder

trait PlayResponseDecoder extends ResponseDecoder {

  private val id: Format[JsValue] = Format[JsValue](Reads(JsSuccess(_)), Writes(identity))

  private def decode[A](body: String)(transform: Reads[JsValue])(implicit reads: Reads[A]): Exception \/ A =
    \/.fromTryCatchNonFatal(Json.parse(body))
      .leftMap{ e => new Exception(s"Parse failure: Invalid json: ${body}", e) }
      .flatMap { j => (transform andThen reads).reads(j) match {
        case JsSuccess(a, _) => a.right
        case JsError(e) => new Exception(s"Parse failure: ${JsError.toJson(e).toString}").left
      }}

  override protected def decodeErrorInfo(body: String): Exception \/ ErrorInfo =
    decode[ErrorInfo](body)((__ \ "errorInfo")(0).json.pick)

  override protected def decodeChallenges(body: String): Exception \/ Vector[Challenge] =
    decode[Vector[Challenge]](body)((__ \ "challenge").json.pick andThen jsMap((__ \ "textOrImageAndChoice").json.pick))

  override protected def decodeInstitution(body: String): Exception \/ Institution =
    decode[Institution](body)((__ \ "keys").json.update((__ \ "key").json.pick).map(identity))

  override protected def decodeInstitutions(body: String): Exception \/ Vector[InstitutionSummary] =
    decode[Vector[InstitutionSummary]](body)((__ \ "institution").json.pick)

  override protected def decodeAccount(body: String): Exception \/ Account =
    decode[Account](body)((__ \ "accounts")(0).json.pick)

  override protected def decodeAccounts(body: String): Exception \/ Vector[Account] =
    decode[Vector[Account]](body)((__ \ "accounts").json.pick)

  override protected def decodePositions(body: String): Exception \/ Vector[Position] =
    decode[Vector[Position]](body)((__ \ "position").json.pick)

  override protected def decodeTransactions(body: String): Exception \/ TransactionsResponse =
    decode[TransactionsResponse](body)(id)

  // Utility functions
  private def traverse[ A <: JsValue](as: JsArray)(f: JsValue => JsResult[A]): JsResult[JsArray] = as match {
    case JsArray(Nil) => JsSuccess(JsArray(Nil))
    case JsArray(h +: t) => f(h).flatMap(fh => traverse(JsArray(t))(f).map(fh +: _))
  }

  private def jsMap[ A <: JsValue](f: Reads[A]): Reads[JsValue] = Reads {
    case as: JsArray => traverse(as)(f.reads)
    case _ => JsError("error.expected.array")
  }

}
