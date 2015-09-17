package scintuit.contrib.play

import play.api.data.validation.ValidationError
import play.api.libs.json._

object apiTransforms extends ApiTransforms

trait ApiTransforms {

  private def traverse[ A <: JsValue](as: JsArray)(f: JsValue => JsResult[A]): JsResult[JsArray] = as match {
    case JsArray(Nil) => JsSuccess(JsArray(Nil))
    case JsArray(h +: t) => f(h).flatMap(fh => traverse(JsArray(t))(f).map(fh +: _))
  }

  private def jsMap[ A <: JsValue](f: Reads[A]): Reads[JsValue] = Reads {
    case as: JsArray => traverse(as)(f.reads)
    case _ => JsError("error.expected.array")
  }

  val idT: Reads[JsValue] = __.json.pick
  val errorInfoT: Reads[JsValue] = (__ \ "errorInfo")(0).json.pick

  val listInstitutionsT: Reads[JsValue] = (__ \ "institution").json.pick
  val getInstitutionT: Reads[JsValue] = (__ \ "keys").json.update((__ \ "key").json.pick).map(identity)

  val listAccountsT: Reads[JsValue] = (__ \ "accounts").json.pick
  val getAccountT: Reads[JsValue] = (__ \ "accounts")(0).json.pick
  val addAccountsT: Writes[JsValue] = (__ \ "credentials" \ "credential").write
  val addAccountsChallengeT: Writes[JsValue] = (__ \ "challengeResponses" \ "response").write
  val challengeIssuedT: Reads[JsValue] = (__ \ "challenge").json.pick andThen jsMap((__ \ "textOrImageAndChoice").json.pick)

  val listPositionsT: Reads[JsValue] = (__ \ "position").json.pick
}
