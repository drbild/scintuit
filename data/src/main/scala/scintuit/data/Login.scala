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
