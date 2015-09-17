package scintuit.contrib.http4s

import org.http4s.client.Client
import org.http4s.client.oauth1._
import org.http4s._
import org.http4s.Http4s._
import scintuit.Customer
import scintuit.auth.{SamlAssertion, IntuitConfig, OAuthConsumer, OAuthToken}

import scalaz.concurrent.Task

object OAuth extends IntuitDecoders {
  val signing = Signing.apply _

  def tokenForCustomer(client: Client, config: IntuitConfig, customer: Customer): Task[OAuthToken] = {
    val samlUri = uri("https://oauth.intuit.com/oauth/v1/get_access_token_by_saml")
    val request = Request(Method.POST, samlUri)
    .putHeaders(Header("Authorization", s"""OAuth oauth_consumer_key="${config.oauthConsumer.key}""""))
    .withBody(UrlForm("saml_assertion" -> SamlAssertion(config.signingKey, config.samlProvider, customer).body))
    client(request).as[OAuthToken]
  }

  private implicit def convertConsumer(consumer: OAuthConsumer): Consumer = Consumer(consumer.key, consumer.secret)
  private implicit def convertToken(token: OAuthToken): Token = Token(token.token, token.secret)

  private object Signing {
    def apply(consumer: OAuthConsumer, token: OAuthToken)(client: Client): Client = new Client {
      override def shutdown(): Task[Unit] = client.shutdown

      override def prepare(req: Request): Task[Response] = {
        val r = signRequest(req, consumer, callback = None, verifier = None, token = Some(token))
        client.prepare(r)
      }
    }
  }
}
