package scintuit.data.raw

import enumeratum.EnumEntry._
import enumeratum.{Enum, EnumEntry}

/**
 * Module for categorization types
 */
object categorization {

  sealed abstract class CategorizationSource extends EnumEntry with Uppercase
  object CategorizationSource extends Enum[CategorizationSource] {
    val values = findValues
    case object Aggr extends CategorizationSource
    case object OFX extends CategorizationSource
    case object Cat extends CategorizationSource
  }

  case class Categorization(
    common: CategorizationCommon,
    context: Vector[CategorizationContext]
  )

  case class CategorizationCommon(
    normalizedPayeeName: Option[String],
    merchant: Option[String],
    sic: Option[Int]
  )

  case class CategorizationContext(
    source: Option[CategorizationSource],
    categoryName: Option[String],
    contextType: Option[String],
    scheduleC: Option[String]
  )

}
