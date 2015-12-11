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

import org.http4s._
import org.http4s.client.Client
import org.http4s.headers.Accept

import scalaz.concurrent.Task

object PrepFor {
  val prepFor = PrepFor.apply _

  def apply(decoder: EntityDecoder[_])(client: Client): Client = new Client {
    override def shutdown(): Task[Unit] = client.shutdown()



    override def prepare(req: Request): Task[Response] = {
      val r = if (decoder.consumes.nonEmpty) {
        val m = decoder.consumes.toList
        req.putHeaders(Accept(m.head, m.tail: _*))
      } else req
      client.prepare(r)
    }
  }

}
