package scintuit.util

import scintuit.Customer
import scintuit.Intuit.{IntuitIO, IntuitOp}
import scintuit.util.request._
import scintuit.util.response.{IntuitResponse, ResponseDecoder}

import scalaz._

object interpreter {

  case class Context[T, C: Customer](op: IntuitOp[T], customer: C)

  type Stage[M[_], T, C, A] = Kleisli[M, Context[T, C], A]

  object Stage {
    def apply[M[_], T, C, A](run: Context[T, C] => M[A]) = Kleisli.kleisli(run)
  }

  abstract class Interpreter[M[_] : Monad : Catchable](
    implicit
    encoder: RequestEncoder,
    decoder: ResponseDecoder
  ) {

    protected def execute[T, C: Customer](request: IntuitRequest): Stage[M, T, C, IntuitResponse]

    def interpK[C: Customer]: IntuitOp ~> Kleisli[M, C, ?] = new (IntuitOp ~> Kleisli[M, C, ?]) {
      def apply[A](op: IntuitOp[A]): Kleisli[M, C, A] = {
        def kleisli[S, T](run: S => Stage[M, A, C, T]) = Kleisli[Stage[M, A, C, ?], S, T](run)

        (kleisli(encoder.encode[M, A, C]) >=> kleisli(execute[A, C]) >=> kleisli(decoder.decode[M, A, C]))
        .run(op)
        .local[C](c => Context(op, c))
      }
    }

    def transK[C: Customer]: IntuitIO ~> Kleisli[M, C, ?] = new (IntuitIO ~> Kleisli[M, C, ?]) {
      def apply[A](ma: IntuitIO[A]): Kleisli[M, C, A] =
        Free.runFC[IntuitOp, Kleisli[M, C, ?], A](ma)(interpK)
    }

    def transFor[C: Customer](customer: C): IntuitIO ~> M = new (IntuitIO ~> M) {
      override def apply[A](ma: IntuitIO[A]): M[A] =
        transK.apply(ma)(customer)
    }
  }
}
