package scintuit.util.monocle
package syntax

import monocle._

final class IsoOps[S, A](self: Iso[S, A]) {
  final def mapIso(f: Iso[A, A]): Iso[S, S] = MapOptic.mapIso(self, f)
  final def mapPrism(f: Prism[A, A]): Prism[S, S] = MapOptic.mapPrism(self.asPrism, f)
  final def mapLens(f: Lens[A, A]): Lens[S, S] = MapOptic.mapLens(self.asLens, f)
  final def mapOptional(f: Optional[A, A]): Optional[S, S] = MapOptic.mapOptional(self.asOptional, f)
}

trait ToIsoOps {
  implicit def isoOps[S, A](s: Iso[S, A]): IsoOps[S, A] = new IsoOps(s)
}
