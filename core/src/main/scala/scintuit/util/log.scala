package scintuit.util

import com.github.nscala_time.time.Imports._
import org.log4s._
import scintuit.raw.customer.Customer
import scintuit.util.capture.Capture
import scintuit.util.http.{Response, Request}

import scala.concurrent.duration.FiniteDuration
import scalaz.Monad

object log {

  val httpLogger = getLogger("scintuit.http")

  def logHttp[M[_]: Monad : Capture, C: Customer](
    customer: C,
    start: DateTime,
    request: Request,
    response: Response,
    elapsed: FiniteDuration
  ): M[Unit] = Capture[M].apply{
      httpLogger.info(s"""${Customer[C].name(customer)} [${start.withZone(DateTimeZone.UTC).toString}] "${request.method} ${request.uri}" ${response.status} ${elapsed.toMillis}""")
    }

}
