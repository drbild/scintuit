package scintuit.contrib.play

import jawn._
import play.api.libs.json._

import scala.collection.mutable

object Parser extends SupportParser[JsValue] {

  implicit val facade: Facade[JsValue] =
    new Facade[JsValue] {
      def jnull() = JsNull
      def jfalse() = JsBoolean(false)
      def jtrue() = JsBoolean(true)
      def jnum(s: String) = JsNumber(BigDecimal(s))
      def jint(s: String) = JsNumber(BigDecimal(s))
      def jstring(s: String) = JsString(s)

      def singleContext() =
        new FContext[JsValue] {
          var value: JsValue = null
          def add(s: String) { value = jstring(s) }
          def add(v: JsValue) { value = v }
          def finish: JsValue = value
          def isObj: Boolean = false
        }

      def arrayContext() =
        new FContext[JsValue] {
          val vs = mutable.ListBuffer.empty[JsValue]
          def add(s: String) { vs += jstring(s) }
          def add(v: JsValue) { vs += v }
          def finish: JsValue = JsArray(vs.toList)
          def isObj: Boolean = false
        }

      def objectContext() =
        new FContext[JsValue] {
          var key: String = null
          var vs = List.empty[(String, JsValue)]
          def add(s: String): Unit =
            if (key == null) key = s
            else {vs = (key, jstring(s)) :: vs; key = null}
          def add(v: JsValue): Unit = { vs = (key, v) :: vs; key = null }
          def finish: JsValue = JsObject(vs)
          def isObj: Boolean = true
        }
    }
}
