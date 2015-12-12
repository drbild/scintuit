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

import scintuit.intuit.IntuitOp
import scintuit.customer.Customer
import scintuit.data._

case class IntuitError(
  request: String,
  customer: String,
  statusCode: Int,
  errorInfo: ErrorInfo
) extends Exception(
  s"Intuit API Error (customer=$customer, request=$request, statusCode=$statusCode): ${errorInfo}"
)

object IntuitError {
  def apply[C: Customer](op: IntuitOp[_], customer: C, statusCode: Int, errorInfo: ErrorInfo): IntuitError =
   IntuitError(op.toString, implicitly[Customer[C]].name(customer), statusCode, errorInfo)
}
