package scintuit.contrib.play

import play.api.libs.oauth.{ConsumerKey, OAuthCalculator, RequestToken}
import play.api.libs.ws.WSRequest
import scintuit.util.oauth.{OAuthConsumer, OAuthToken}

object oauth {
 val sign = Sign.apply _

  private implicit def convertConsumer(consumer: OAuthConsumer): ConsumerKey = ConsumerKey(consumer.key, consumer.secret)
  private implicit def convertToken(token: OAuthToken): RequestToken = RequestToken(token.token, token.secret)

  private object Sign {
    def apply(consumer: OAuthConsumer, token: OAuthToken)(request: WSRequest): WSRequest =
      request.sign(OAuthCalculator(consumer, token))
    }

}
