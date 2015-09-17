import sbt._
import Keys._

import de.heikoseeberger.sbtheader.AutomateHeaderPlugin
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport._
import de.heikoseeberger.sbtheader.license.Apache2_0

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
      Libs.scalazConcurrent,
      Libs.nscalaTime,
      Libs.nscalaMoney
    ),

    // Release options
    organization    := "io.tellur",
    pomExtra        := pomExtraVal,
    pomPostProcess  := pomPostProcessVal,
    credentials    ++= credentialsVal,

    // keep headers updated
    headers         := headersVal
  )

  val pomExtraVal: xml.NodeBuffer = (
    <url>https://github.com/drbild/scintuit</url>
      <licenses>
        <license>
          <name>Apache</name>
          <url>http://www.opensource.org/licenses/Apache-2.0</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
    <scm>
      <url>git@github.com:drbild/scintuit.git</url>
      <connection>scm:git:git@github.com:drbild/scintuit.git</connection>
    </scm>
    <developers>
      <developer>
        <id>drbild</id>
        <name>David R. Bild</name>
        <url>https://github.com/drbild</url>
      </developer>
    </developers>
  )

  /* strip test deps from pom */
  import scala.xml._
  import scala.xml.transform._
  lazy val pomPostProcessVal = { node: Node =>
    def stripIf(f: Node => Boolean) = new RewriteRule {
      override def transform(n: Node) = if (f(n)) NodeSeq.Empty else n
    }
    val stripTestScope = stripIf(n => n.label == "dependency" && (n \ "scope").text == "test")
    new RuleTransformer(stripTestScope).transform(node)(0)
  }

  val credentialsVal: Seq[Credentials] = {
    val realm    = "Sonatype Nexus Repository Manager"
    val host     = "oss.sonatype.org"
    val cred = for {
      username <- scala.util.Try(sys.env("NEXUS_USERNAME")).toOption
      password <- scala.util.Try(sys.env("NEXUS_PASSWORD")).toOption
    } yield Credentials(realm, host, username, password)
    cred.toList
  }

  val headersVal = Map(
    "scala" -> Apache2_0("2015", "David R. Bild", "*")
  )
}

object ScintuitProject {
  import Common._

  def apply(name: String): Project = ScintuitProject(name, file(name))
  def apply(name: String, file: File): Project =  Project(name, file).settings(commonSettings:_*)
}

object ContribProject {
  def apply(name: String): Project = ScintuitProject(s"contrib-${name}", file(s"contrib/${name}"))
}
