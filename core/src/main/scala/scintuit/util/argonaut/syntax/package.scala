package scintuit.util.argonaut

package object syntax {
  object codecJson extends ToCodecJsonOps
  object decodeJson extends ToDecodeJsonOps
  object encodeJson extends ToEncodeJsonOps
  object prism extends ToPrismOps
  object optional extends ToOptionalOps

  object all extends ToAllOps
}
