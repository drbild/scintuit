import sbt._
import Keys._

object Dependencies {
  val resolvers = Seq(
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
  )

  // Versions
  object V {
    val scala       = "2.11.7"
    val scalaz      = "7.1.5"
    val enumeratum  = "1.3.1"
    val http4s      = "0.11.1"
    val jawn        = "0.8.3"
    val nscalaTime  = "2.0.0"
    val nscalaMoney = "0.10.0"
    val play        = "2.4.2"
    val playJsonExt = "0.5.0"
  }

  // Libraries
  object Libs {
    val scalaz              = "org.scalaz"              %% "scalaz-core"            % V.scalaz
    val scalazConcurrent    = "org.scalaz"              %% "scalaz-concurrent"      % V.scalaz
    val enumeratum          = "com.beachape"            %% "enumeratum"             % V.enumeratum
    val enumeratumPlayJson  = "com.beachape"            %% "enumeratum-play-json"   % V.enumeratum
    val http4sClient        = "org.http4s"              %% "http4s-blaze-client"    % V.http4s
    val http4sJawn          = "org.http4s"              %% "http4s-jawn"            % V.http4s
    val jawnPlay            = "org.spire-math"          %% "jawn-play"              % V.jawn
    val nscalaTime          = "com.github.nscala-time"  %% "nscala-time"            % V.nscalaTime
    val nscalaMoney         = "com.github.nscala-money" %% "nscala-money"           % V.nscalaMoney
    val nscalaMoneyPlayJson = "com.github.nscala-money" %% "nscala-money-play-json" % V.nscalaMoney
    val playJson            = "com.typesafe.play"       %% "play-json"              % V.play
    val playJsonExt         = "org.cvogt"               %% "play-json-extensions"   % V.playJsonExt
  }
}
