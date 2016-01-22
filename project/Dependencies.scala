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
    val caffeine    = "2.1.0"
    val enumeratum  = "1.3.2"
    val http4s      = "0.11.3"
    val jsr305      = "3.0.1"
    val log4s       = "1.2.1"
    val nscalaTime  = "2.6.0"
    val nscalaMoney = "0.11.0"
    val play        = "2.4.6"
    val playJsonExt = "0.6.1"
  }

  // Libraries
  object Libs {
    val scalaz              = "org.scalaz"                    %% "scalaz-core"            % V.scalaz
    val scalazConcurrent    = "org.scalaz"                    %% "scalaz-concurrent"      % V.scalaz
    val caffeine            = "com.github.ben-manes.caffeine" %  "caffeine"               % V.caffeine
    val enumeratum          = "com.beachape"                  %% "enumeratum"             % V.enumeratum
    val enumeratumPlayJson  = "com.beachape"                  %% "enumeratum-play-json"   % V.enumeratum
    val http4sClient        = "org.http4s"                    %% "http4s-blaze-client"    % V.http4s
    val log4s               = "org.log4s"                     %% "log4s"                  % V.log4s
    val nscalaTime          = "com.github.nscala-time"        %% "nscala-time"            % V.nscalaTime
    val nscalaMoney         = "com.github.nscala-money"       %% "nscala-money"           % V.nscalaMoney
    val nscalaMoneyPlayJson = "com.github.nscala-money"       %% "nscala-money-play-json" % V.nscalaMoney
    val playJson            = "com.typesafe.play"             %% "play-json"              % V.play
    val playJsonExt         = "org.cvogt"                     %% "play-json-extensions"   % V.playJsonExt
    val playWs              = "com.typesafe.play"             %% "play-ws"                % V.play
    val jsr305              = "com.google.code.findbugs"      %  "jsr305"                 % V.jsr305 % "provided"
  }
}
