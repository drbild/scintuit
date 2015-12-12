package scintuit.util

import scintuit.Customer
import scintuit.util.auth.AuthConfig
import scintuit.util.http.{Response, Request}

import scalaz.{Monad, Catchable, \/}
import scalaz.std.option._
import scalaz.syntax.monad._
import scalaz.syntax.either._

import scala.language.postfixOps

object oauth {

  case class OAuthConsumer(key: String, secret: String)
  case class OAuthToken(token: String, secret: String)

  case class OAuthException(msg: String) extends Exception(msg)

  def buildTokenRequest[C: Customer](customer: C, config: AuthConfig): Request = {
    val assertion = saml.signedAssertion(config.signingKey, config.samlIssuer, Customer[C].name(customer))
    http.request(http.Post, "https://oauth.intuit.com/oauth/v1/get_access_token_by_saml")
      .withHeader("Authorization", s"""OAuth oauth_consumer_key="${config.oauthConsumer.key}"""")
      .withHeader("Content-Type", "application/x-www-form-urlencoded")
      .withHeader("Accepts", "text/plain")
      .withBody( s"""saml_assertion=${java.net.URLEncoder.encode(assertion, "UTF-8")}""")
  }

  def decodeTokenResponse(response: Response): OAuthException \/ OAuthToken = {
    def parse(contents: String): Map[String, String] = contents.split("&") map { _ split("=", 2) match {
      case Array(k, v) => (k, v)
      case Array(k) => (k, "")
    }} toMap

    object Token {
      def unapply(body: String): Option[(String, String)] = {
        val params = parse(body)
        (params.get("oauth_token") |@| params.get("oauth_token_secret")).tupled
      }
    }

    response match {
      case Response(200, _, Token(token, secret)) => OAuthToken(token, secret).right
      case Response(200, _, body) => OAuthException(s"Failed to parse response body: ${body}").left
      case Response(code, _, body) => OAuthException(s"Failed with status code $code: ${body}").left
    }
  }

  def fetchToken[M[_]: Monad: Catchable, C: Customer](execute: Request => M[Response])(config: AuthConfig, customer: C): M[OAuthToken] =
    for {
      response <- execute(buildTokenRequest(customer, config))
      result   <- decodeTokenResponse(response).fold(Catchable[M].fail, (Monad[M].point[OAuthToken](_)))
    } yield result

}
