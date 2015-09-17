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
