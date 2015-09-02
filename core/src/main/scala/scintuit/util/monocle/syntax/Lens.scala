package scintuit.util.monocle.syntax

import monocle._
import scintuit.util.monocle.MapOptic

final class LensOps[S, A](self: Lens[S, A]) {
  final def mapIso(f: Iso[A, A]): Lens[S, S] = MapOptic.mapLens(self, f.asLens)
  final def mapPrism(f: Prism[A, A]): Optional[S, S] = MapOptic.mapOptional(self.asOptional, f.asOptional)
  final def mapLens(f: Lens[A, A]): Lens[S, S] = MapOptic.mapLens(self, f)
  final def mapOptional(f: Optional[A, A]): Optional[S, S] = MapOptic.mapOptional(self.asOptional, f)
}

trait ToLensOps {
  implicit def lensOps[S, A](s: Lens[S, A]): LensOps[S, A] = new LensOps(s)
}
