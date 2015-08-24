import sbt._
import Keys._

object Dependencies {
  // Versions
  object V {
    val scala      = "2.11.7"
    val scalaz     = "7.1.3"
    val argonaut   = "6.1"
    val http4s     = "0.9.1"
    val nscalaTime = "2.0.0"
  }

  // Libraries
  object Libs {
    val scalaz           = "org.scalaz"             %% "scalaz-core"         % V.scalaz
    val scalazConcurrent = "org.scalaz"             %% "scalaz-concurrent"   % V.scalaz
    val argonaut         = "io.argonaut"            %% "argonaut"            % V.argonaut
    val http4sClient     = "org.http4s"             %% "http4s-blaze-client" % V.http4s
    val http4sArgonaut   = "org.http4s"             %% "http4s-argonaut"     % V.http4s
    val nscalaTime       = "com.github.nscala-time" %% "nscala-time"         % V.nscalaTime
  }
}
