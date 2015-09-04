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
  challenges: List[Challenge]
)

// ------------------- Challenge -------------------
sealed trait Challenge

case class TextChallenge(
  text: String
) extends Challenge

case class ImageChallenge(
  image: Base64Binary
) extends Challenge

// ---------------- ChallengeAnswer -----------------
sealed trait ChallengeAnswer

case class TextChallengeAnswer (
  text: String,
  value: String
) extends ChallengeAnswer

case class ImageChallengeAnswer (
  text: String,
  value: String
) extends ChallengeAnswer

// ====================== Responses ======================
sealed trait AddAccountsResponse
sealed trait UpdateLoginResponse

case class AccountsAdded(accounts: Seq[Account]) extends AddAccountsResponse
case object LoginUpdated extends UpdateLoginResponse
case class ChallengeIssued(challengeSession: ChallengeSession) extends AddAccountsResponse with UpdateLoginResponse
