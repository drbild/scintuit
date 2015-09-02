package scintuit.util.monocle.syntax

import monocle._
import scintuit.util.monocle.MapOptic

final class OptionalOps[S, A](self: Optional[S, A]) {
  final def mapIso(f: Iso[A, A]): Optional[S, S] = MapOptic.mapOptional(self, f.asOptional)
  final def mapPrism(f: Prism[A, A]): Optional[S, S] = MapOptic.mapOptional(self, f.asOptional)
  final def mapLens(f: Lens[A, A]): Optional[S, S] = MapOptic.mapOptional(self, f.asOptional)
  final def mapOptional(f: Optional[A, A]): Optional[S, S] = MapOptic.mapOptional(self, f)
}

trait ToOptionalOps {
  implicit def optionalOps[S, A](s: POptional[S, S, A, A]): OptionalOps[S, A] = new OptionalOps(s)
}
