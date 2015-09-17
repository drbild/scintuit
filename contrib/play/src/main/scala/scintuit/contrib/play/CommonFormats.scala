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
