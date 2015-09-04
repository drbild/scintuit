import sbt._
import Keys._

import Dependencies.{Libs, V}

object Common {
  val commonSettings: Seq[Setting[_]] = Seq(
    scalaVersion := V.scala,

    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
    scalacOptions ++= Seq("-Xfatal-warnings", "-Ywarn-unused", "-Ywarn-unused-import"),
    scalacOptions ++= Seq("-language:implicitConversions", "-language:higherKinds"),

    updateOptions := updateOptions.value.withCachedResolution(true),

    resolvers     ++= Dependencies.resolvers,

    libraryDependencies ++= Seq(
      Libs.scalaz,
      Libs.scalazConcurrent
    )
  )
}

object ScintuitProject {
  import Common._

  def apply(name: String): Project = ScintuitProject(name, file(name))
  def apply(name: String, file: File): Project =  Project(name, file).settings(commonSettings:_*)
}
