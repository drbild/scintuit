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

import enumeratum.EnumEntry._
import enumeratum.{Enum, EnumEntry}

case class Address(
  address1: Option[String],
  address2: Option[String],
  address3: Option[String],
  city: Option[String],
  state: Option[String],
  postalCode: Option[String],
  country: Option[String]
)

sealed trait NotRefreshedReason extends EnumEntry with Snakecase with Uppercase
object NotRefreshedReason extends Enum[NotRefreshedReason] {
  val values = findValues

  case object NotNecessary extends NotRefreshedReason
  case object CredentialsRequired extends NotRefreshedReason
  case object ChallengeResponseRequired extends NotRefreshedReason
  case object Unavailable extends NotRefreshedReason
}

case class Status(
  statusMessage: Option[String],
  errorInfo: Option[ErrorInfo]
)

sealed trait ErrorType extends EnumEntry with Snakecase with Uppercase
object ErrorType extends Enum[ErrorType] {
  val values = findValues

  case object SystemError extends ErrorType
  case object AppError extends ErrorType
  case object UserError extends ErrorType
  case object Warning extends ErrorType
}

case class ErrorInfo(
  errorType: Option[ErrorType],
  errorCode: Option[ErrorCode],
  errorMessage: Option[String],
  correlationId: Option[String]
)

case class ErrorCode(code: String) {
  def isSuccess: Boolean = code == ErrorCode.Success
  def isError: Boolean = !isSuccess
}

object ErrorCode {
  val Success = "0"

  def extract(codes: ErrorCode*)(error: ErrorInfo): Option[ErrorCode] = error.errorCode match {
    case Some(ec) if codes.contains(ec) => Some(ec)
    case _ => None
  }

  def extractS(codes: String*)(error: ErrorInfo): Option[ErrorCode] =
    extract((codes map ErrorCode.apply): _*)(error)
}
