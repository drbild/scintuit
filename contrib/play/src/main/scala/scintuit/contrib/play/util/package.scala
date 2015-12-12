package scintuit.contrib.play

import scintuit.contrib.play.util.decode.PlayDecoder
import scintuit.contrib.play.util.encode.PlayEncoder

package object util {

  implicit object Implicits extends Implicits

  trait Implicits extends PlayEncoder with PlayDecoder
}
