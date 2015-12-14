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

import scintuit.data.api.account._
import scintuit.data.api.institution._
import scintuit.data.raw

/**
 * Module for institution login resources
 */
object login {

  type LoginId = raw.login.LoginId

  type ChallengeSessionId = raw.login.ChallengeSessionId
  type ChallengeNodeId = raw.login.ChallengeNodeId
  type ChallengeAnswer = raw.login.ChallengeAnswer
  type Base64Binary = raw.login.Base64Binary

  type Credentials = raw.login.Credentials
  type ChallengeSession = raw.login.ChallengeSession
  type Choice = raw.login.Choice
  type Challenge = raw.login.Challenge
  type TextChallenge = raw.login.TextChallenge
  type ImageChallenge = raw.login.ImageChallenge

  val Credentials = raw.login.Credentials
  val ChallengeSession = raw.login.ChallengeSession
  val Choice = raw.login.Choice
  val TextChallenge = raw.login.TextChallenge
  val ImageChallenge = raw.login.ImageChallenge

  type LoginError = raw.login.LoginError
  type AlreadyLoggedOn = raw.login.AlreadyLoggedOn
  type ChallengeIssued = raw.login.ChallengeIssued
  type IncorrectChallengeAnswer = raw.login.IncorrectChallengeAnswer
  type InterventionRequired = raw.login.InterventionRequired
  type IncorrectCredentials = raw.login.IncorrectCredentials
  type IncorrectPersonalAccessCode = raw.login.IncorrectPersonalAccessCode
  type TemporarilyUnavailable = raw.login.TemporarilyUnavailable

  val LoginError = raw.login.LoginError
  val AlreadyLoggedOn = raw.login.AlreadyLoggedOn
  val ChallengeIssued = raw.login.ChallengeIssued
  val IncorrectChallengeAnswer = raw.login.IncorrectChallengeAnswer
  val InterventionRequired = raw.login.InterventionRequired
  val IncorrectCredentials = raw.login.IncorrectCredentials
  val IncorrectPersonalAccessCode = raw.login.IncorrectPersonalAccessCode
  val TemporarilyUnavailable = raw.login.TemporarilyUnavailable

  // ====================== Logins ======================
  case class Login(
    id: LoginId,
    institutionId: InstitutionId,
    accounts: Vector[Account]
  )

}
