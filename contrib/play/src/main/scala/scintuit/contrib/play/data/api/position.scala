package scintuit.contrib.play.data.api
import play.api.libs.json._

import scintuit.data.api.position._
import scintuit.contrib.play.data.raw

object position {

  object PositionFormats extends PositionFormats

  trait PositionFormats {
    import raw.position.{PositionFormats => RawPositionFormats}

    implicit val positionFormat: Format[Position] =
        xmap(RawPositionFormats.positionFormat)(Position.apply, _.raw)

    private def xmap[A, B](format: Format[A])(fab: A => B, fba: B => A): Format[B] =
      Format(format map fab, Writes(b => format.writes(fba(b))))

  }

}
