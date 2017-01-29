package com.abhi

/**
  * Created by ASrivastava on 1/25/17.
  */

import org.http4s.client.blaze.SimpleHttp1Client
import org.http4s.Status.ResponseClass.Successful
import io.circe.syntax._
import org.http4s._
import org.http4s.headers._
import org.http4s.circe._
import scalaz.concurrent.Task
import io.circe._

final case class Login(username: String, password: String)
final case class Token(token: String)

object JsonHelpers {
   import io.circe.generic.auto._
   implicit val loginEntityEncoder : EntityEncoder[Login] = jsonEncoderOf[Login]
   implicit val loginEntityDecoder : EntityDecoder[Login] = jsonOf[Login]
   implicit val tokenEntityEncoder: EntityEncoder[Token] = jsonEncoderOf[Token]
   implicit val tokenEntityDecoder : EntityDecoder[Token] = jsonOf[Token]
}

object Http4sTest2 extends App {
   import JsonHelpers._
   val url = "http://"
   val uri = Uri.fromString(url).valueOr(throw _)
   val list = List[Header](`Content-Type`(MediaType.`application/json`), `Accept`(MediaType.`application/json`))
   val request = Request(uri = uri, method = Method.POST)
      .withBody(Login("foo", "bar").asJson)
      .map{r => r.replaceAllHeaders(list :_*)}.run
   val client = SimpleHttp1Client()
   val result = client.fetch[Option[Token]](request){
      case Successful(response) => response.as[Token].map(Some(_))
      case _ => Task(Option.empty[Token])
   }.run
   println(result)
}
