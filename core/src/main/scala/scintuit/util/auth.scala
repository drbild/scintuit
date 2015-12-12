package scintuit.util

import java.security.PrivateKey

import scintuit.util.oauth.OAuthConsumer
import scintuit.util.saml.SamlIssuer

object auth {

  case class AuthConfig(
    signingKey: PrivateKey,
    samlIssuer: SamlIssuer,
    oauthConsumer: OAuthConsumer
  )

}
