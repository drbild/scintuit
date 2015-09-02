package scintuit.util.argonaut.syntax

import argonaut.{DecodeJson, Json}
import monocle.Optional
import scintuit.util.argonaut.{codecJson => o}

final class OptionalOps[A](self: Optional[Json, A]) {
  final def toDecodeJson: DecodeJson[A] = o.decodeJson(self)
}

trait ToOptionalOps {
  implicit def ToOptionalOps[A](a: Optional[Json, A]): OptionalOps[A] = new OptionalOps[A](a)
}
