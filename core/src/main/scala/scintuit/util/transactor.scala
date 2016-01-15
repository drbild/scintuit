package scintuit.util

import scintuit.raw.customer.{Customer, CustomerIO, CustomerOp}
import scintuit.util.auth.AuthConfig
import scintuit.util.cache.Cache
import scintuit.util.capture.Capture

import scintuit.util.http.{Response, Request, Executor}
import scintuit.util.oauth.OAuthToken
import scintuit.util.parse.Decoder
import scintuit.util.prepare.Encoder

import scalaz._
import scalaz.syntax.monad._

object transactor {

  class Transactor[M[_] : Monad : Capture : Catchable](
    config: AuthConfig,
    cache: Cache[M, String, OAuthToken],
    executor: Executor[M]
  )(
    implicit
    encode: Encoder,
    decode: Decoder
  ) {

    import scintuit.util.time._

    def logFor[C: Customer](customer: C, execute: Request => M[Response])(request: Request): M[Response] =
      for {
         start              <- now[M]
         timedResponse      <- timed(execute)(request)
         _                  <- log.logHttp[M, C](customer, start, request, timedResponse._2, timedResponse._1)
      } yield timedResponse._2

    def findToken[C: Customer](customer: C): M[OAuthToken] =
      cache.get(Customer[C].name(customer), oauth.fetchToken(logFor(customer, executor.execute))(config, _))

    def interpK[C: Customer]: CustomerOp ~> Kleisli[M, C, ?] = new (CustomerOp ~> Kleisli[M, C, ?]) {
      def apply[A](op: CustomerOp[A]): Kleisli[M, C, A] =
        for {
          customer <- Kleisli.ask[M, C]
          token    <- findToken(customer).liftM[Kleisli[?[_], C, ?]]
          request  <- prepare.prepareRequest(encode)(op).point[Kleisli[M, C, ?]]
          response <- logFor(customer, executor.sign(config.oauthConsumer, token))(request).liftM[Kleisli[?[_], C, ?]]
          result   <- parse.parseResponse[M, C, A](decode)(customer, op, response).liftM[Kleisli[?[_], C, ?]]
        } yield result
    }

    def transK[C: Customer]: CustomerIO ~> Kleisli[M, C, ?] = new (CustomerIO ~> Kleisli[M, C, ?]) {
      def apply[A](ma: CustomerIO[A]): Kleisli[M, C, A] =
        ma.foldMap[Kleisli[M, C, ?]](interpK)

        //Free.runFC[CustomerOp, Kleisli[M, C, ?], A](ma)(interpK)
    }

    def trans[C: Customer](customer: C): CustomerIO ~> M = new (CustomerIO ~> M) {
      override def apply[A](ma: CustomerIO[A]): M[A] =
        transK.apply(ma)(customer)
    }
  }

}
