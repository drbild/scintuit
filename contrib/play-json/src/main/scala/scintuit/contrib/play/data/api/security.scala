package scintuit.contrib.play.data.api
import play.api.libs.json._

import scintuit.data.api.security._
import scintuit.contrib.play.data.raw

object security {

  object SecurityFormats extends SecurityFormats

  trait SecurityFormats {

    import raw.security.{SecurityFormats => RawSecurityFormats}

    implicit val securityFormat: Format[Option[Security]] =
      xmap(RawSecurityFormats.securityInfoFormat)(_ map Security.fromRaw, _ map (_.raw))

    private def xmap[A, B](format: Format[A])(fab: A => B, fba: B => A): Format[B] =
      Format(format map fab, Writes(b => format.writes(fba(b))))

  }

}
