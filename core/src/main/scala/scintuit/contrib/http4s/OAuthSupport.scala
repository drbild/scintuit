package scintuit.contrib.http4s

import org.http4s.client.oauth1.{Consumer, Token}
import scintuit.auth.{OAuthConsumer, OAuthToken}

trait OAuthSupport {

  implicit def convertConsumer(consumer: OAuthConsumer): Consumer = Consumer(consumer.key, consumer.secret)

  implicit def convertToken(token: OAuthToken): Token = Token(token.token, token.secret)

}
