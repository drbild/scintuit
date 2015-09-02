package scintuit.contrib.argonaut

import argonaut.Argonaut._
import argonaut.{Json, CodecJson}
import monocle.Prism
import scintuit.data.Address

trait CommonCodecs {
  implicit def AddressCodecJson: CodecJson[Address] = casecodec7(Address.apply, Address.unapply)(
    "address1",
    "address2",
    "address3",
    "city",
    "state",
    "postalCode",
    "country"
  )
}
