package scintuit.util

import scalaz.concurrent.Task

trait Capture[M[_]] {
  def apply[A](a: => A): M[A]
}

object Capture {
  def apply[M[_]](implicit M: Capture[M]): Capture[M] = M

  implicit val TaskCapture: Capture[Task] =
    new Capture[Task] {
      def apply[A](a: => A): Task[A] =
        Task.delay(a)
    }
}
