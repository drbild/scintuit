package scintuit

import scintuit.data._

sealed trait LoginError
sealed trait ChallengeError
case class InvalidCredentials(errorCode: ErrorCode) extends LoginError
case class IncorrectChallengeAnswer(errorCode: ErrorCode) extends ChallengeError
case class InterventionRequired(errorCode: ErrorCode) extends LoginError with ChallengeError

case class ErrorResponse(httpStatus: Int, status: Option[Status]) extends Exception
