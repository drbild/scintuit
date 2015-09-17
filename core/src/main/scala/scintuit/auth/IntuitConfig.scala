package scintuit.auth

import java.security.PrivateKey

case class OAuthConsumer(key: String, secret: String)

case class OAuthToken(token: String, secret: String)

case class SamlProvider(id: String) {
  override def toString: String = id
}

case class IntuitConfig(
  signingKey: PrivateKey,
  samlProvider: SamlProvider,
  oauthConsumer: OAuthConsumer
)
