package scintuit.util

import scintuit.util.oauth.{OAuthConsumer, OAuthToken}

object http {

  // ------------------------ HTTP Request ------------------------
  sealed trait Method
  case object DELETE extends Method
  case object GET extends Method
  case object POST extends Method
  case object PUT extends Method

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
