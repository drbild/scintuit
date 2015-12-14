package scintuit.contrib.play.data.api

import play.api.libs.json._

import scintuit.data.api.institution._
import scintuit.contrib.play.data.raw

object institution {

  object InstitutionFormats extends InstitutionFormats

  trait InstitutionFormats {
    import raw.institution.{InstitutionFormats => RawInstitutionFormats}

    implicit val addressFormat: Format[Address] = RawInstitutionFormats.addressFormat

    implicit val keyFormat: Format[Key] =
      xmap(RawInstitutionFormats.keyFormat)(Key.apply, _.raw)

    implicit val institutionSummaryFormat: Format[InstitutionSummary] =
      xmap(RawInstitutionFormats.institutionFormat)(InstitutionSummary.apply, _.raw)

    implicit val institutionFormat: Format[Institution] =
      xmap(RawInstitutionFormats.institutionDetailsFormat)(Institution.apply, _.raw)

    private def xmap[A, B](format: Format[A])(fab: A => B, fba: B => A): Format[B] =
      Format(format map fab, Writes(b => format.writes(fba(b))))

  }

}
