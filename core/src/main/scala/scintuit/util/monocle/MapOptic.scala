package scintuit.util.monocle

import monocle._

import scala.language.postfixOps

object MapOptic extends MapOpticFunctions

trait MapOpticFunctions {
  def mapIso[S, A](selection: Iso[S, A], f: Iso[A, A]): Iso[S, S] =
    Iso[S, S](s => selection.modify(f.get)(s)
    )(a => selection.modify(f.reverseGet)(a))

  def mapPrism[S, A](selection: Prism[S, A], f: Prism[A, A]): Prism[S, S] =
    Prism[S, S](s => selection.getOption(s) flatMap f.getOption map selection.reverseGet
    )(a => selection.modify(f.reverseGet)(a))

  def mapLens[S, A](selection: Lens[S, A], f: Lens[A, A]): Lens[S, S] =
    Lens[S, S](s => selection.modify(f.get)(s)
    )(t => s => selection.modify(f.set(_)(selection.get(s)))(t))

  def mapOptional[S, A](selection: Optional[S, A], f: Optional[A, A]): Optional[S, S] =
    Optional[S, S](s => selection.getOption(s) flatMap f.getOption map (selection.set(_)(s))
    )(t => s => selection.getOrModify(s) map (a => selection.modify(f.set(_)(a))(t)) merge)
}
