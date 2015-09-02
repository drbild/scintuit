package scintuit.util.monocle

package object syntax {
  object iso extends ToIsoOps
  object prism extends ToPrismOps
  object lens extends ToLensOps
  object optional extends ToOptionalOps

  object all extends ToAllOps
}
