package scintuit.contrib.play

import play.api.libs.functional.syntax._
import play.api.libs.json._
import scintuit.data._

object LoginFormats extends LoginFormats

trait LoginFormats {
  implicit val credentialsFormat: Format[Credentials] = Json.format[Credentials]

  // ==================================== Challenge =====================================
  implicit val choiceFormat: Format[Choice] = Format[Choice](
    ((__ \ "text").read[String] and
      (__ \ "val").read[String])(Choice),
    ((__ \ "text").write[String] and
      (__ \ "val").write[String])(unlift(Choice.unapply))
  )

  private def readChoices(seq: Seq[JsValue]): JsResult[Option[Vector[Choice]]] = seq match {
    case Seq() => JsSuccess(None)
    case c => JsArray(c).validate[Vector[Choice]].map(Some(_))
  }

  private def writeChoices(choices: Option[Vector[Choice]]): Seq[JsValue] =
    choices.toList.flatten.map(__.write[Choice].writes)

  implicit val textChallengeFormat: Format[TextChallenge] = Format[TextChallenge](
    Reads[TextChallenge] {
      case JsArray(text +: choices) => (text.validate[String] and readChoices(choices))(TextChallenge)
      case _ => JsError("error.expected.jsarray")
    },
    Writes[TextChallenge](cc => JsString(cc.text) +: JsArray(writeChoices(cc.choices)))
  )

  implicit val imageChallengeFormat: Format[ImageChallenge] = Format[ImageChallenge](
    Reads[ImageChallenge] {
      case JsArray(text +: image +: choices) =>
        (text.validate[String] and image.validate[Base64Binary] and readChoices(choices))(ImageChallenge)
      case _ => JsError("error.expected.jsarray")
    },
    Writes[ImageChallenge](cc => JsString(cc.text) +: JsString(cc.image) +: JsArray(writeChoices(cc.choices)))
  )

  implicit val challengeFormat: Format[Challenge] = Format[Challenge](
    (textChallengeFormat.map(x => x: Challenge) orElse
      imageChallengeFormat.map(x => x: Challenge)),
    Writes[Challenge](_ match {
      case text: TextChallenge => textChallengeFormat.writes(text)
      case image: ImageChallenge => imageChallengeFormat.writes(image)
    })
  )
}
