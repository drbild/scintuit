import Dependencies._
import Common._

lazy val scintuit = (
  ScintuitProject("scintuit", file("."))
    settings(
      packagedArtifacts := Map.empty // don't publish the default aggregate root project
    )
    aggregate(core, data, contribPlay, contribHttp4s)
    dependsOn(core, data, contribPlay, contribHttp4s)
)

lazy val core = (
  ScintuitProject("core")
    settings(
      name                 :=  "scintuit-core",
      libraryDependencies ++= Seq(
        Libs.caffeine,
        Libs.jsr305
      )
    )
    dependsOn(data)
)

lazy val data = (
  ScintuitProject("data")
    settings(
      name                := "scintuit-data",
      libraryDependencies ++= Seq(
        Libs.enumeratum
      )
    )
)

lazy val contribPlay = (
  ContribProject("play")
    settings(
      name                := "scintuit-contrib-play",
      libraryDependencies ++=  Seq(
        Libs.enumeratum,
        Libs.enumeratumPlayJson,
        Libs.nscalaMoneyPlayJson,
        Libs.playJson,
        Libs.playJsonExt
      )
    )
    dependsOn(core, data)
)

lazy val contribHttp4s = (
  ContribProject("http4s")
    settings(
      name                := "scintuit-contrib-http4s",
      libraryDependencies ++= Seq(
        Libs.http4sClient
      )
    )
    dependsOn(core, data, contribPlay)
)
