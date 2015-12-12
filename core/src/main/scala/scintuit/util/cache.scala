package scintuit.util

import java.util.function
import com.github.benmanes.caffeine.cache.{Caffeine, Cache => CaffeineCache}
import scintuit.util.capture.Capture

import scala.concurrent.{Promise, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

import scalaz.{\/-, -\/, Monad}
import scalaz.concurrent.Task
import scalaz.syntax.either._

object cache {

  abstract class Cache[M[_]: Monad: Capture, K <: AnyRef, V] {

    def get(key: K): Option[M[V]]

    def get(key: K, gen: K => M[V]): M[V]

    def invalidate(key: K): Unit
  }

  object ExpiringLruCache {

    def apply[M[_]: Monad: Capture, K <: AnyRef, V](
      maximumSize: Int,
      expireAfterWrite: Duration
    )(implicit ec: ExecutionContext): Cache[M, K, V] = new ExpiringLruCache[M, K, V](maximumSize, expireAfterWrite)
  }

  class ExpiringLruCache[M[_]: Monad: Capture, K <: AnyRef, V](
    maximumSize: Int,
    expireAfterWrite: Duration
  )(implicit ec: ExecutionContext) extends Cache[M, K, V] {

    private val cache: CaffeineCache[K, Future[V]] =
      Caffeine.newBuilder()
        .expireAfterWrite(expireAfterWrite.length, expireAfterWrite.unit)
        .maximumSize(maximumSize)
        .build()

    def get(key: K): Option[M[V]] = Option(cache getIfPresent key) map toM

    def get(key: K, gen: K => M[V]): M[V] =
      toM(cache.get(key, toJavaFunction(gen andThen toFuture)) andThen { case Failure(e) => invalidate(key) })

    def invalidate(key: K): Unit = cache.invalidate(key)

    private def toM[A](future: Future[A]): M[A] =
      Capture[M].async { register =>
        future.onComplete {
          case Success(a) => register(a.right)
          case Failure(e) => register(e.left)
        }
      }

    private def toFuture[A](m: M[A]): Future[A] = {
      val p: Promise[A] = Promise()
      Capture[M].runAsync(m) {
        case -\/(e) => p.failure(e)
        case \/-(a) => p.success(a)
      }
      p.future
    }

    private def toJavaFunction[T, R](f: T => R): java.util.function.Function[T, R] =
      new function.Function[T, R] { override def apply(t: T): R = f(t) }

  }

}
