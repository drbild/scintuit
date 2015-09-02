package scintuit

import scintuit.data._
import scintuit.util.Capture

import scalaz._

case class Scope(customer: String)

object IntuitApi {
  /**
   * ADT for Intuit API operations
   */
  sealed trait IntuitOp[A]

  object IntuitOp {
    case object ListInstitution extends IntuitOp[Seq[InstitutionSummary]]
    case class GetInstitution(id: Long) extends IntuitOp[Institution]
  }

  import IntuitOp._

  /**
   * Free monad over a free functor of [[IntuitOp]].
   */
  type IntuitIO[A] = Free.FreeC[IntuitOp, A]

  /**
   * Monad instance for [[IntuitIO]] (can't be be inferred).
   */
  implicit val MonadIntuitIO: Monad[IntuitIO] = Free.freeMonad[({type λ[α] = Coyoneda[IntuitOp, α]})#λ]

  val institutions: IntuitIO[Seq[InstitutionSummary]] =
    Free.liftFC(ListInstitution)

  def institution(id: Long): IntuitIO[Institution] =
    Free.liftFC(GetInstitution(id))

  val program =
  for {
    all <- institutions
    inst <- institution(1)
  } yield inst
}
