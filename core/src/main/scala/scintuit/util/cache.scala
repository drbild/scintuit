package scintuit.util

import java.util.function
import com.github.benmanes.caffeine.cache.{Caffeine, Cache => CaffeineCache}

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.util.Failure

object cache {

  abstract class Cache[K <: AnyRef, V] {
    def get(key: K): Option[Future[V]]

    def get(key: K, gen: K => Future[V]): Future[V]

    def invalidate(key: K): Unit
  }

  final class ExpiringLruCache[K <: AnyRef, V](
    maximumSize: Int,
    expireAfterWrite: Duration
  )(implicit ec: ExecutionContext) extends Cache[K, V] {

    private val cache: CaffeineCache[K, Future[V]] =
      Caffeine.newBuilder()
        .expireAfterWrite(expireAfterWrite.length, expireAfterWrite.unit)
        .maximumSize(maximumSize)
        .build()

    def get(key: K): Option[Future[V]] = Option(cache getIfPresent key)

    def get(key: K, gen: K => Future[V]): Future[V] = cache.get(key, toJavaFunction(gen)) andThen {
      case Failure(e) => invalidate(key)
    }

    def invalidate(key: K): Unit = cache.invalidate(key)

    private def toJavaFunction[T, R](f: T => R): java.util.function.Function[T, R] =
      new function.Function[T, R] { override def apply(t: T): R = f(t) }
  }

  object ExpiringLruCache {
    def apply[K <: AnyRef, V](maximumSize: Int, expireAfterWrite: Duration)(implicit ec: ExecutionContext): Cache[K, V] =
      new ExpiringLruCache[K, V](maximumSize, expireAfterWrite)
  }

}
