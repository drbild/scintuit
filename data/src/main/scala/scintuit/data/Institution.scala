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

package scintuit.data

import com.github.nscala_money.money.Imports._

case class Key(
  name: String,
  value: Option[String],
  status: String,
  valueLengthMin: Option[Int],
  valueLengthMax: Option[Int],
  displayFlag: Boolean,
  displayOrder: Int,
  mask: Boolean,
  instructions: Option[String],
  description: Option[String]
)

case class InstitutionSummary(
  id: InstitutionId,
  name: String,
  homeUrl: Option[String],
  phoneNumber: Option[String],
  virtual: Option[Boolean]
)

case class Institution (
  id: InstitutionId,
  name: String,
  homeUrl: Option[String],
  phoneNumber: Option[String],
  virtual: Option[Boolean],
  address: Address,
  emailAddress: Option[String],
  specialText: Option[String],
  currencyCode: Option[CurrencyUnit],
  keys: Seq[Key]
)
