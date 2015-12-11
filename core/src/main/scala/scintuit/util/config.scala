package scintuit.util

import java.security.PrivateKey

import scintuit.util.oauth.OAuthConsumer
import scintuit.util.saml.SamlIssuer

object config {

  case class IntuitConfig(
    signingKey: PrivateKey,
    samlIssuer: SamlIssuer,
    oauthConsumer: OAuthConsumer
  )

}
