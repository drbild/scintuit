package scintuit.contrib.play

import play.api.libs.ws._
import scintuit.util.capture.Capture
import scintuit.util.http
import scintuit.util.http.{Request, Response, Executor}
import scintuit.util.oauth.{OAuthConsumer, OAuthToken}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}
import scalaz.Monad
import scalaz.syntax.either._
import scalaz.syntax.monad._


object executor {

  class PlayWsExecutor[M[_] : Monad : Capture](ws: WSClient)(implicit ec: ExecutionContext) extends Executor[M] {

    private val client: WSRequest => M[WSResponse] =
      req => toM(req.execute())

    private def execute(request: Request, client: WSRequest => M[WSResponse]): M[Response] =
      convertRequest(request).point[M] >>= client >>= (convertResponse _ andThen (_.point[M]))

    override def execute(request: http.Request): M[Response] =
       execute(request, client)

    override def sign(consumer: OAuthConsumer, token: OAuthToken)(request: Request): M[Response] =
       execute(request, oauth.sign(consumer, token) andThen client)

    private def convertRequest(request: Request): WSRequest = {
      val wsRequest = ws.url(request.uri)
        .withMethod(request.method.toString)
        .withHeaders(request.headers.toVector: _*)
      request.body match {
        case Some(body) => wsRequest.withBody(body)
        case None => wsRequest
      }
    }

    private def convertResponse(wsResponse: WSResponse): Response =
      Response(
        wsResponse.status,
        wsResponse.allHeaders mapValues (_ applyOrElse(0, (_: Int) => "")) toSet, // only keep first header value
        wsResponse.body
      )

    private def toM[A](future: Future[A]): M[A] =
      Capture[M].async { register =>
        future.onComplete {
          case Success(a) => register(a.right)
          case Failure(e) => register(e.left)
        }
      }

  }

}
