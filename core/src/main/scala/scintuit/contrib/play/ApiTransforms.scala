package scintuit.contrib.play

import play.api.libs.json._

object apiTransforms extends ApiTransforms

trait ApiTransforms {
  val idT: Reads[JsValue] = __.json.pick

  val listInstitutionsT: Reads[JsValue] = (__ \ "institution").json.pick
  val getInstitutionT: Reads[JsValue] = (__ \ "keys").json.update((__ \ "key").json.pick).map(identity)

  val listAccountsT: Reads[JsValue] = (__ \ "accounts").json.pick
  val getAccountT: Reads[JsValue] = (__ \ "accounts")(0).json.pick
}
