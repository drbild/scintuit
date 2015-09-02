package scintuit.util.argonaut

import argonaut._
import monocle._

import scalaz.syntax.std.option._

object codecJson extends CodecJsonOptics

trait CodecJsonOptics {
  def as[A](codecJson: CodecJson[A]): Prism[Json, A] =
    as[A](codecJson.Encoder, codecJson.Decoder)

  def as[A](implicit encode: EncodeJson[A], decode: DecodeJson[A]): Prism[Json, A] =
    Prism[Json, A](decode.decodeJson(_).toOption)(encode.encode(_))

  def codecJson[A](p: Prism[Json, A], error: String = "invalid json"): CodecJson[A] =
    CodecJson.derived[A](encodeJson(p), decodeJson(p.asOptional, error))

  def decodeJson[A](o: Optional[Json, A], error: String = "invalid json"): DecodeJson[A] =
    DecodeJson[A](hc => o.getOption(hc.focus).cata(
      DecodeResult.ok(_),
      DecodeResult.fail(error, hc.history)
    ))

  def encodeJson[A](o: Prism[Json, A]): EncodeJson[A] =
    EncodeJson[A](o.reverseGet)

}
