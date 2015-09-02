package scintuit.util.argonaut.syntax

import argonaut.{CodecJson, Json}
import monocle.Prism
import scintuit.util.argonaut.{codecJson => o}

final class PrismOps[A](self: Prism[Json, A]) {
  final def toCodecJson: CodecJson[A] = o.codecJson(self)
}

trait ToPrismOps {
  implicit def ToPrismOps[A](p: Prism[Json, A]): PrismOps[A] = new PrismOps[A](p)
}
