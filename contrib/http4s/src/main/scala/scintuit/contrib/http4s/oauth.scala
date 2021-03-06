/*
 * Copyright 2015 David R. Bild
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scintuit.contrib.http4s

import org.http4s.client.Client
import org.http4s.client.oauth1._
import org.http4s._
import scintuit.util.oauth.{OAuthConsumer, OAuthToken}

import scalaz.concurrent.Task

object oauth {
  val signing = Signing.apply _

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
