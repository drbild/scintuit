package scintuit.contrib.http4s

import org.http4s.client.Client
import org.http4s.client.oauth1._
import org.http4s.{Header, Request, Response}
import scintuit.auth.{OAuthConsumer, OAuthToken}

import scalaz.concurrent.Task

object SignRequests extends OAuthSupport {

  def apply(consumer: OAuthConsumer, token: OAuthToken)(client: Client): Client = new Client {
    override def shutdown(): Task[Unit] = client.shutdown

    override def prepare(req: Request): Task[Response] = {
      val r = signRequest(req, consumer, callback = None, verifier = None, token = Some(token))
        .map(_.putHeaders(Header("Accept", "application/json")))
      client.prepare(r)
    }
  }
}

trait OAuthSupport {
  implicit def convertConsumer(consumer: OAuthConsumer): Consumer = Consumer(consumer.key, consumer.secret)
  implicit def convertToken(token: OAuthToken): Token = Token(token.token, token.secret)
}
