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

package scintuit.data.api

import com.github.nscala_money.money.Imports._
import scintuit.data.raw

/**
 * Module for institution resources
 */
object institution {

  type InstitutionId = raw.institution.InstitutionId

  type Address = raw.institution.Address
  val Address = raw.institution.Address

  type RawKey = raw.institution.Key
  val RawKey = raw.institution.Key

  type RawInstitution = raw.institution.Institution
  type RawInstitutionDetails = raw.institution.InstitutionDetails
  val RawInstitution = raw.institution.Institution
  val RawInstitutionDetails = raw.institution.InstitutionDetails

  case class Key(raw: RawKey) {
    def name: String = raw.name
    def value: Option[String] = raw.`val`
    def status: String = raw.status
    def valueLengthMin: Option[Int] = raw.valueLengthMin
    def valueLengthMax: Option[Int] = raw.valueLengthMax
    def displayFlag: Boolean = raw.displayFlag
    def displayOrder: Int= raw.displayOrder
    def mask: Boolean = raw.mask
    def instructions: Option[String] = raw.instructions
    def description: Option[String] = raw.description
  }

  case class InstitutionSummary(raw: RawInstitution) {
    def id: InstitutionId = raw.institutionId
    def name: String = raw.institutionName
    def phone: Option[String] = raw.phoneNumber
    def url: Option[String] = raw.homeUrl
    def virtual: Boolean = raw.virtual getOrElse false
  }

  case class Institution(raw: RawInstitutionDetails) {
    def id: InstitutionId = raw.institutionId
    def name: String = raw.institutionName
    def phone: Option[String] = raw.phoneNumber
    def url: Option[String] = raw.homeUrl
    def virtual: Boolean = raw.virtual getOrElse false

    def address: Address = raw.address
    def currency: Option[CurrencyUnit] = raw.currencyCode
    def email: Option[String] = raw.emailAddress
    def keys: Vector[Key]= raw.keys map Key
    def specialText: Option[String] = raw.specialText
  }

}
