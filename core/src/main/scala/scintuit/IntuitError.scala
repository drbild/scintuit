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

package scintuit

import scintuit.data._

case class IntuitError(uri: String, statusCode: Int, errorInfo: ErrorInfo) extends
Exception(s"Got ${statusCode} for ${uri}. ${errorInfo}")

sealed trait LoginError extends Exception
sealed trait ChallengeError extends Exception

case class InvalidCredentials(errorCode: ErrorCode) extends LoginError
case class IncorrectChallengeAnswer(errorCode: ErrorCode) extends ChallengeError
case class InterventionRequired(errorCode: ErrorCode) extends LoginError with ChallengeError

object InvalidCredentials {
  def unapply(error: ErrorInfo): Option[ErrorCode] =
    ErrorCode.extractS("103")(error)
}

object IncorrectChallengeAnswer {
  def unapply(error: ErrorInfo): Option[ErrorCode] =
    ErrorCode.extractS("187")(error)
}

object InterventionRequired {
  def unapply(error: ErrorInfo): Option[ErrorCode] =
    ErrorCode.extractS("101", "108", "109", "179")(error)
}
