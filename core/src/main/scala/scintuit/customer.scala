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

package scintuit

/* Module for typeclass for representation of an Intuit customer */
object customer {

  trait Customer[T] {
    def name(customer: T): String
  }

  object Customer {
    def apply[C: Customer]: Customer[C] = implicitly[Customer[C]]

    implicit object StringCustomer extends Customer[String] {
      def name(customer: String): String = customer
    }
  }

}
