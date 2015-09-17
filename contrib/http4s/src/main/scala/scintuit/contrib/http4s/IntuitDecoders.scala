package scintuit.contrib.http4s

import org.http4s._
import scintuit.auth.OAuthToken

import scalaz.Scalaz._

trait IntuitDecoders {
  implicit def oauthTokenIntuitDecoder(implicit defaultCharset: Charset = DefaultCharset): EntityDecoder[OAuthToken] =
    UrlForm.entityDecoder.flatMapR(form => {
      val token = form.getFirst("oauth_token").toSuccess("missing oauth_token".point[List])
      val secret = form.getFirst("oauth_token_secret").toSuccess("missing oauth_token_secret".point[List])
      (token |@| secret)(OAuthToken(_, _)).fold(
        details => DecodeResult.failure(ParseFailure("", details.mkString(","))),
        DecodeResult.success(_))
    })
}
