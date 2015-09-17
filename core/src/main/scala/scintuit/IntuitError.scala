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
