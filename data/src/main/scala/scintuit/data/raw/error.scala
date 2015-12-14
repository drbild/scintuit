package scintuit.data.raw

import enumeratum.EnumEntry._
import enumeratum.{Enum, EnumEntry}

/**
 * Module for error types
 */
object error {

  type CorrelationId = String

  sealed trait ErrorType extends EnumEntry with Snakecase with Uppercase

  object ErrorType extends Enum[ErrorType] {
    val values = findValues

    case object SystemError extends ErrorType
    case object AppError extends ErrorType
    case object UserError extends ErrorType
    case object Warning extends ErrorType
  }

  case class ErrorCode(code: String) {
    def isSuccess: Boolean = this == ErrorCode.Success
    def isError: Boolean = !isSuccess
  }

  object ErrorCode extends ErrorCodeExtractors {
    val Success = ErrorCode("0")
  }

  case class ErrorInfo(
    errorType: Option[ErrorType],
    errorCode: Option[ErrorCode],
    errorMessage: Option[String],
    correlationId: Option[CorrelationId]
  )

  object ErrorInfo extends ErrorCodeExtractors

  /**
   * Extractors for error codes that identify similar conditions
   */
  trait ErrorCodeExtractors {

    private def matches(codes: String*)(error: ErrorCode): Option[ErrorCode] =
      if (codes contains error.code) Some(error) else (None)

    trait ErrorCodeExtractor {
      def codes: Seq[String]

      def unapply(code: ErrorCode): Option[ErrorCode] = matches(codes: _*)(code)
      def unapply(info: ErrorInfo): Option[ErrorCode] = info.errorCode flatMap unapply
    }

    object AlreadyLoggedOn extends ErrorCodeExtractor {def codes = Vector("179")}
    object IncorrectCredentials extends ErrorCodeExtractor {def codes = Vector("103")}
    object IncorrectPersonalAccessCode extends ErrorCodeExtractor {def codes = Vector("199")}
    object IncorrectChallengeAnswer extends ErrorCodeExtractor {def codes = Vector("187")}
    object InterventionRequired extends ErrorCodeExtractor {def codes = Vector("101", "108", "109", "179")}
    object MissingCredentials extends ErrorCodeExtractor {def codes = Vector("185")}
    object TemporarilyUnavailable extends ErrorCodeExtractor {def codes = Vector("105, 155")}
  }

}
