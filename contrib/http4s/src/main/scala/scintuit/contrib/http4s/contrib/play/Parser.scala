package scintuit.contrib.http4s.contrib.play

import jawn._
import play.api.libs.json._

object Parser extends SupportParser[JsValue] {

  implicit val facade: SimpleFacade[JsValue] =
    new SimpleFacade[JsValue] {
      def jnull() = JsNull
      def jfalse() = JsBoolean(false)
      def jtrue() = JsBoolean(true)
      def jnum(s: String) = JsNumber(BigDecimal(s))
      def jint(s: String) = JsNumber(BigDecimal(s))
      def jstring(s: String) = JsString(s)
      def jarray(vs: List[JsValue]) = JsArray(vs)
      def jobject(vs: Map[String, JsValue]) = JsObject(vs)
    }
}
