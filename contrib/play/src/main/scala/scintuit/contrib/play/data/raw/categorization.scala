package scintuit.contrib.play.data.raw

import enumeratum.EnumFormats
import org.cvogt.play.json.Jsonx
import org.cvogt.play.json.implicits.optionWithNull
import play.api.libs.json.Format

import scintuit.data.raw.categorization._

object categorization {

  object CategorizationFormats extends CategorizationFormats

  trait CategorizationFormats {

    implicit val categorizationSourceFormat: Format[CategorizationSource] = EnumFormats.formats(CategorizationSource, false)

    implicit val categorizationCommonFormat = Jsonx.formatCaseClass[CategorizationCommon]
    implicit val categorizationContextFormat = Jsonx.formatCaseClass[CategorizationContext]
    implicit val categorizationFormat = Jsonx.formatCaseClass[Categorization]
  }

}
