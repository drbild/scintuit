package scintuit.contrib.argonaut

import argonaut.Argonaut._
import argonaut.Json
import monocle.Monocle._
import monocle._
import scintuit.util.monocle.syntax.all._

object apiOptics extends ApiOptics

/** Optics to view Intuit responses as sane Json */
trait ApiOptics {
  private def path(segments: JsonField*): Optional[Json, Json] =
    segments
      .map(jObjectPrism composeOptional index(_))
      .reduceLeft(_ composeOptional _)


  val institutionsO = path("institution")
  val institutionO = path("keys") mapOptional path("key")
}
