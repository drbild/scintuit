package scintuit.contrib.http4s.contrib.play

import org.http4s._
import org.http4s.headers.`Content-Type`
import org.http4s.jawn.jawnDecoder
import play.api.libs.json.{JsValue, Json, Reads, Writes}
import Parser.facade

trait PlayJsonInstances {
  implicit lazy val json: EntityDecoder[JsValue] = jawnDecoder(facade)

  def jsonOf[A](implicit reader: Reads[A]): EntityDecoder[A] =
    json.flatMapR { json =>
      reader.reads(json).fold(
        invalid = errors =>
          DecodeResult.failure {
            println(Json.prettyPrint(json))
            errors.headOption.fold(ParseFailure("Unknown error", "Unknown error")) { case (path, error) =>
              ParseFailure(error.mkString(" "), path.toString)
            }
          },
        valid = (a) => DecodeResult.success(a)
      )
    }

  implicit lazy val jsonEncoder: EntityEncoder[JsValue] =
    EntityEncoder.stringEncoder(Charset.`UTF-8`).contramap[JsValue] { json =>
      Json.prettyPrint(json)
    }.withContentType(`Content-Type`(MediaType.`application/json`))

  def jsonEncoderOf[A](implicit writer: Writes[A]): EntityEncoder[A] =
    jsonEncoder.contramap[A](writer.writes)
}
