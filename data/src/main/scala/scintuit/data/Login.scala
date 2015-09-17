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

// ====================== Login ======================
case class Credentials(
  name: String,
  value: String
)

// ====================== MFA ======================
case class ChallengeSession (
  session: ChallengeSessionId,
  node: ChallengeNodeId,
  challenges: Vector[Challenge]
)

// ------------------- Challenge -------------------
case class Choice(text: String, value: String)

sealed trait Challenge {
  val choices: Option[Vector[Choice]]
}

case class TextChallenge(
  text: String,
  choices: Option[Vector[Choice]]
) extends Challenge

case class ImageChallenge(
  text: String,
  image: Base64Binary,
  choices: Option[Vector[Choice]]
) extends Challenge

// ====================== Responses ======================
sealed trait AddAccountsResponse
sealed trait UpdateLoginResponse

case class AccountsAdded(accounts: Vector[Account]) extends AddAccountsResponse
case object LoginUpdated extends UpdateLoginResponse
case class ChallengeIssued(challengeSession: ChallengeSession) extends AddAccountsResponse with UpdateLoginResponse
object ChallengeIssued {
  def apply(session: ChallengeSessionId, node: ChallengeNodeId, challenges: Vector[Challenge]): ChallengeIssued =
    ChallengeIssued(ChallengeSession(session, node, challenges))
}
