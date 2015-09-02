package scintuit.util.argonaut.syntax

import argonaut.{EncodeJson, DecodeJson, Json}
import monocle.Prism
import scintuit.util.argonaut.{codecJson => o}

final class DecodeJsonOps[A](self: DecodeJson[A]) {
  final def toPrism(implicit encode: EncodeJson[A]): Prism[Json, A] = o.as(encode, self)
}

trait ToDecodeJsonOps {
  implicit def ToDecodeJsonOps[A](a: DecodeJson[A]): DecodeJsonOps[A] = new DecodeJsonOps[A](a)
}
