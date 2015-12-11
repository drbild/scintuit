package scintuit.util

object oauth {

  case class OAuthConsumer(key: String, secret: String)
  case class OAuthToken(token: String, secret: String)

}
