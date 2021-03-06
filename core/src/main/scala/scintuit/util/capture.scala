/*
 * Copyright 2015 David R. Bild
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scintuit.util

import scalaz.\/
import scalaz.concurrent.Task

/**
 * Module for a typeclass for monads with an effect-capturing unit.
 */
object capture {
  trait Capture[M[_]] {

    def apply[A](a: => A): M[A]
    def async[A](register: ((Throwable \/ A) => Unit) => Unit): M[A]
    def runAsync[A](ma: M[A])(f: (Throwable \/ A) => Unit): Unit
  }

  object Capture {
    def apply[M[_]](implicit M: Capture[M]): Capture[M] = M

    implicit val TaskCapture: Capture[Task] =
      new Capture[Task] {

        def apply[A](a: => A): Task[A] = Task.delay(a)
        def async[A](register: ((Throwable \/ A) => Unit) => Unit): Task[A] = Task.async(register)
        def runAsync[A](ma: Task[A])(f: (Throwable \/ A) => Unit): Unit = ma.runAsync(f)
      }
  }
}
