package scintuit.data.raw

import scintuit.data.raw.error.ErrorCode

/**
 * Module for login resources
 */
object login {

  type LoginId = Long

  type ChallengeSessionId = String
  type ChallengeNodeId = String
  type ChallengeAnswer = String
  type Base64Binary = String

  // ====================== Credentials ======================
  case class Credentials(
    name: String,
    value: String
  )

  // ====================== Challenges ======================
  case class ChallengeSession(
    session: ChallengeSessionId,
    node: ChallengeNodeId,
    challenges: Vector[Challenge]
  )

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

  // ====================== Errors ======================
  sealed trait LoginError extends Exception

  case class AlreadyLoggedOn(code: ErrorCode) extends LoginError
  case class IncorrectChallengeAnswer(code: ErrorCode) extends LoginError
  case class InterventionRequired(code: ErrorCode) extends LoginError
  case class IncorrectCredentials(code: ErrorCode) extends LoginError
  case class IncorrectPersonalAccessCode(code: ErrorCode) extends LoginError
  case class TemporarilyUnavailable(code: ErrorCode) extends LoginError
  
  case class ChallengeIssued(challengeSession: ChallengeSession) extends LoginError

  object LoginError {

    def errorCode(code: ErrorCode): Option[LoginError] = code match {
      case ErrorCode.AlreadyLoggedOn(code) => Some(AlreadyLoggedOn(code))
      case ErrorCode.IncorrectChallengeAnswer(code) => Some(IncorrectChallengeAnswer(code))
      case ErrorCode.InterventionRequired(code) => Some(InterventionRequired(code))
      case ErrorCode.IncorrectCredentials(code) => Some(IncorrectCredentials(code))
      case ErrorCode.IncorrectPersonalAccessCode(code) => Some(IncorrectPersonalAccessCode(code))
      case ErrorCode.TemporarilyUnavailable(code) => Some(TemporarilyUnavailable(code))
    }

    def challengeIssued(session: ChallengeSessionId, node: ChallengeNodeId, challenges: Vector[Challenge]): LoginError =
      ChallengeIssued(ChallengeSession(session, node, challenges))

  }

}
