package scintuit.util.argonaut.syntax

import argonaut.{EncodeJson, DecodeJson, Json}
import monocle.Prism
import scintuit.util.argonaut.{codecJson => o}

final class EncodeJsonOps[A](self: EncodeJson[A]) {
  final def toPrism(implicit decode: DecodeJson[A]): Prism[Json, A] = o.as(self, decode)
}

trait ToEncodeJsonOps {
  implicit def ToEncodeJsonOps[A](a: EncodeJson[A]): EncodeJsonOps[A] = new EncodeJsonOps[A](a)
}
