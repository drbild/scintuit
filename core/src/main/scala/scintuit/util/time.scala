package scintuit.util

import com.github.nscala_time.time.Imports._
import scintuit.util.capture.Capture

import scala.concurrent.duration._
import scalaz.Monad
import scalaz.syntax.monad._

/**
 * Module for helper functions for timing executions
 */
object time {

  def now[M[_] : Monad : Capture]: M[DateTime] = Capture[M].apply(DateTime.now())
  def nowMonotonic[M[_] : Monad : Capture]: M[Long] =  Capture[M].apply(System.nanoTime())

  def timed[M[_] : Monad : Capture, A, B](f: A => M[B])(a: A): M[(FiniteDuration, B)] =
    for {
      begin <- nowMonotonic
      b     <- f(a)
      end   <- nowMonotonic
    } yield ((end - begin).nanoseconds, b)

}
