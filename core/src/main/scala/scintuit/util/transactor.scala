package scintuit.util

import scintuit.Customer
import scintuit.Intuit.{IntuitIO, IntuitOp}
import scintuit.util.auth.AuthConfig
import scintuit.util.parse.Decoder
import scintuit.util.prepare.Encoder
import scintuit.util.http.Executor

import scalaz._
import scalaz.syntax.monad._

object transactor {

  class Transactor[M[_] : Monad : Catchable](
    config: AuthConfig,
    execute: Executor[M]
  )(implicit
    encode: Encoder,
    decode: Decoder
  ) {

    def interpK[C: Customer]: IntuitOp ~> Kleisli[M, C, ?] = new (IntuitOp ~> Kleisli[M, C, ?]) {
      def apply[A](op: IntuitOp[A]): Kleisli[M, C, A] =
        for {
          customer <- Kleisli.ask[M, C]
          token    <- oauth.fetchToken(execute.execute)(config, customer).liftM[Kleisli[?[_], C, ?]]
          request  <- prepare.prepareRequest(encode)(op).point[Kleisli[M, C, ?]]
          response <- execute.execute(request, config.oauthConsumer, token).liftM[Kleisli[?[_], C, ?]]
          result   <- parse.parseResponse[M, C, A](decode)(customer, op, response).liftM[Kleisli[?[_], C, ?]]
        } yield result
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
