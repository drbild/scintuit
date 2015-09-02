package scintuit.util.argonaut.syntax

import argonaut.{JsonObject, CodecJson, Json}
import monocle.Prism
import scintuit.util.argonaut.{codecJson => o}

final class CodecJsonOps[A](self: CodecJson[A]) {
  final def toPrism: Prism[Json, A] = o.as(self.Encoder, self.Decoder)
}

trait ToCodecJsonOps {
  implicit def ToCodecJsonOps[A](a: CodecJson[A]): CodecJsonOps[A] = new CodecJsonOps[A](a)
}
