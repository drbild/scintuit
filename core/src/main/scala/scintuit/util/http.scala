package scintuit.util

import scintuit.util.oauth.{OAuthConsumer, OAuthToken}

object http {

  // ------------------------ HTTP Request ------------------------
  sealed trait Method
  case object Delete extends Method
  case object Get extends Method
  case object Post extends Method
  case object Put extends Method

  case class Request(
    method: Method,
    uri: String,
    headers: Set[(String, String)],
    body: Option[String]
  ) {

    def withBody(body: String): Request =
      this.copy(method, uri, headers, Some(body))

    def withHeader(name: String, value: String): Request =
      this.copy(method, uri, headers + (name -> value), None)
  }

  def request(m: Method, r: String): Request = Request(m, r, Set.empty, None)

  // ------------------------ HTTP Response ------------------------
  case class Response(
    status: Int,
    headers: Set[(String, String)],
    body: String
  )

  // ------------------------ HTTP Execution ------------------------
  trait Executor[M[_]] {

    def execute(request: Request): M[Response]

    def execute(request: Request, consumer: OAuthConsumer, token: OAuthToken): M[Response]
  }

}
