package scintuit.util.monocle.syntax

import monocle._
import scintuit.util.monocle.MapOptic

final class PrismOps[S, A](self: Prism[S, A]) {
  final def mapIso(f: Iso[A, A]): Prism[S, S] = MapOptic.mapPrism(self, f.asPrism)
  final def mapPrism(f: Prism[A, A]): Prism[S, S] = MapOptic.mapPrism(self, f)
  final def mapLens(f: Lens[A, A]): Optional[S, S] = MapOptic.mapOptional(self.asOptional, f.asOptional)
  final def mapOptional(f: Optional[A, A]): Optional[S, S] = MapOptic.mapOptional(self.asOptional, f)
}

trait ToPrismOps {
  implicit def prismOps[S, A](s: Prism[S, A]): PrismOps[S, A] = new PrismOps(s)
}
