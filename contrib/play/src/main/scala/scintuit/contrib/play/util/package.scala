package scintuit.contrib.play

package object util {
  trait Implicits extends PlayRequestEncoder with PlayResponseDecoder

  implicit object Implicits extends Implicits
}
