package scintuit.data.api

import scintuit.data.raw

import scala.language.postfixOps

/**
 * Module for categorization types
 */
object categorization {

  type CategorizationSource = raw.categorization.CategorizationSource
  val CategorizationSource = raw.categorization.CategorizationSource

  type RawCategorization = raw.categorization.Categorization
  type RawCategorizationContext = raw.categorization.CategorizationContext
  type RawCategorizationCommon = raw.categorization.CategorizationCommon

  val RawCategorization = raw.categorization.Categorization
  val RawCategorizationContext = raw.categorization.CategorizationContext
  val RawCategorizationCommon = raw.categorization.CategorizationCommon

  case class CategorizationContext(raw: RawCategorizationContext) {
    def category: Option[String] = raw.categoryName
    def scheduleC: Option[String] = raw.scheduleC

    def source: Option[CategorizationSource] = raw.source
    def contextType: Option[String] = raw.contextType
  }

  case class Categorization(raw: RawCategorization) {
    def payee: Option[String] = raw.common.normalizedPayeeName
    def merchant: Option[String] = raw.common.merchant
    def sic: Option[Int] = raw.common.sic

    def categoriesConsumer: Set[String] = (raw.context map (_.categoryName) flatten).toSet
    def cateogiresScheduleC: Set[String] = (raw.context map (_.scheduleC) flatten).toSet

    def contexts: Vector[CategorizationContext] = raw.context map CategorizationContext
  }

}
