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

package scintuit.contrib.play.data.raw

import com.github.nscala_money.money.json.PlayImports._
import play.api.libs.json._

import scintuit.data.raw.institution._

object institution {

  object InstitutionFormats extends InstitutionFormats

  trait InstitutionFormats {
    implicit val addressFormat: Format[Address] = Json.format[Address]
    implicit val keyFormat: Format[Key] = Json.format[Key]
    implicit val institutionFormat: Format[Institution] = Json.format[Institution]
    implicit val institutionDetailsFormat: Format[InstitutionDetails] = Json.format[InstitutionDetails]
  }

}
