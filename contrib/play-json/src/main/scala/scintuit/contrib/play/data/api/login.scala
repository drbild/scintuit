package scintuit.contrib.play.data.api

import play.api.libs.json._

import scintuit.data.api.login._
import scintuit.contrib.play.data.api
import scintuit.contrib.play.data.raw

object login {

  object LoginFormats extends LoginFormats

  trait LoginFormats {
    import api.account.AccountFormats._
    import raw.login.{LoginFormats => RawLoginFormats}

    implicit val credentialsFormat: Format[Credentials] = RawLoginFormats.credentialsFormat
    implicit val choiceFormat: Format[Choice] = RawLoginFormats.choiceFormat
    implicit val textChallengeFormat: Format[TextChallenge] = RawLoginFormats.textChallengeFormat
    implicit val imageChallengeFormat: Format[ImageChallenge] = RawLoginFormats.imageChallengeFormat
    implicit val challengeFormat: Format[Challenge] = RawLoginFormats.challengeFormat

    implicit val loginFormat: Format[Login] = Json.format[Login]

  }

}
