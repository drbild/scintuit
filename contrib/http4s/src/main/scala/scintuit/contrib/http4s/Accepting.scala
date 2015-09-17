package scintuit.contrib.http4s

import org.http4s.client.Client
import org.http4s.{Header, Request, Response}

import scalaz.concurrent.Task

object Accepting {
  def apply(accepts: String*)(client: Client): Client = new Client {
    override def shutdown(): Task[Unit] = client.shutdown

    override def prepare(req: Request): Task[Response] = {
      val r = req.putHeaders(Header("Accept", accepts mkString ", "))
      client.prepare(r)
    }
  }

  val json = Accepting("application/json") _
}
