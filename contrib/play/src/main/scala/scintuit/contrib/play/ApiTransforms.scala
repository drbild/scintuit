/*
 * Copyright 2015 David R. Bild
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scintuit.contrib.play

import play.api.libs.json._
import scintuit.data._
import scintuit.contrib.play.AccountFormats._

object ApiTransforms extends ApiTransforms

trait ApiTransforms {

  private def traverse[ A <: JsValue](as: JsArray)(f: JsValue => JsResult[A]): JsResult[JsArray] = as match {
    case JsArray(Nil) => JsSuccess(JsArray(Nil))
    case JsArray(h +: t) => f(h).flatMap(fh => traverse(JsArray(t))(f).map(fh +: _))
  }

  private def jsMap[ A <: JsValue](f: Reads[A]): Reads[JsValue] = Reads {
    case as: JsArray => traverse(as)(f.reads)
    case _ => JsError("error.expected.array")
  }

  val idT: Format[JsValue] = Format[JsValue](Reads(JsSuccess(_)), Writes(identity))
  val errorInfoT: Reads[JsValue] = (__ \ "errorInfo")(0).json.pick

  val listInstitutionsT: Reads[JsValue] = (__ \ "institution").json.pick
  val getInstitutionT: Reads[JsValue] = (__ \ "keys").json.update((__ \ "key").json.pick).map(identity)

  val listAccountsT: Reads[JsValue] = (__ \ "accounts").json.pick
  val getAccountT: Reads[JsValue] = (__ \ "accounts")(0).json.pick
  val addAccountsT: Writes[JsValue] = (__ \ "credentials" \ "credential").write
  val addAccountsChallengeT: Writes[JsValue] = (__ \ "challengeResponses" \ "response").write
  val challengeIssuedT: Reads[JsValue] = (__ \ "challenge").json.pick andThen jsMap((__ \ "textOrImageAndChoice").json.pick)

  val updateAccountTypeT: Writes[AccountType] = Writes { typ =>
    val label = typ match {
      case _: BankingAccountType => "banking"
      case _: CreditAccountType => "credit"
      case _: LoanAccountType => "loan"
      case _: InvestmentAccountType => "investment"
      case _: RewardAccountType => "rewards"
    }

    Json.obj(
       "type" -> s"${label}Account",
       s"${label}AccountType" -> Json.toJson(typ)
    )
  }

  val listPositionsT: Reads[JsValue] = (__ \ "position").json.pick
}
