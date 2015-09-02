package scintuit.auth

case class OAuthConsumer(key: String, secret: String)

case class OAuthToken(token: String, secret: String)

case class SamlProvider(id: String) {
  override def toString: String = id
}

case class Customer(id: String) {
  override def toString: String = id
}
