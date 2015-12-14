package scintuit.contrib.play.data.api

import play.api.libs.json._

import scintuit.data.api.categorization._
import scintuit.contrib.play.data.raw

object categorization {

  object CategorizationFormats extends CategorizationFormats

  trait CategorizationFormats {
    import raw.categorization.{CategorizationFormats => RawCategorizationFormats}

    implicit val categorizationSourceFormat: Format[CategorizationSource] = RawCategorizationFormats.categorizationSourceFormat

    implicit val categorizationContextFormat: Format[CategorizationContext] =
      xmap(RawCategorizationFormats.categorizationContextFormat)(CategorizationContext.apply, _.raw)

    implicit val categorizationFormat: Format[Categorization] =
      xmap(RawCategorizationFormats.categorizationFormat)(Categorization.apply, _.raw)

    private def xmap[A, B](format: Format[A])(fab: A => B, fba: B => A): Format[B] =
      Format(format map fab, Writes(b => format.writes(fba(b))))

  }

}
