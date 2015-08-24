import Dependencies._
import Common._

lazy val scintuit = (
  ScintuitProject("scintuit", file("."))
    settings(
      packagedArtifacts := Map.empty // don't publish the default aggregate root project
    )
    aggregate(core)
    dependsOn(core)
)

lazy val core = (
  ScintuitProject("core")
    settings(
      name                 :=  "scintuit-core",
      libraryDependencies  ++= Seq(
        Libs.argonaut,
        Libs.http4sArgonaut,
        Libs.http4sClient,
        Libs.nscalaTime
      )
    )
)
