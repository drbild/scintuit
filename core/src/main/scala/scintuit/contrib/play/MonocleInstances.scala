package scintuit.contrib.play

import monocle._
import monocle.function.{At, Index}
import play.api.libs.json.{JsObject, JsValue}

import scalaz.syntax.std.option._

object monocleInstances extends MonocleInstances

trait MonocleInstances {
  val jsObjectPrism: Prism[JsValue, JsObject] =
    Prism[JsValue, JsObject](
      _ match {
        case j: JsObject => Some(j)
        case _ => None
      }
    )(identity)

  implicit val jsObjectAt: At[JsObject, String, JsValue] = new At[JsObject, String, JsValue] {
    override def at(i: String): Lens[JsObject, Option[JsValue]] =
      Lens[JsObject, Option[JsValue]](_.value.get(i))(a => s => a.cata(s.+(i, _), s - i))
  }

  implicit val jsObjectIndex: Index[JsObject, String, JsValue] = Index.atIndex
}
