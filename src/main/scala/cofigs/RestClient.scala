package cofigs
import akka.actor.{ ActorLogging, Props, Actor, ActorSystem }
import akka.event.Logging
import spray.httpx.SprayJsonSupport
import java.net.URL
import spray.client.pipelining._
import scala.util.{ Failure, Success }
import akka.util.Timeout
import scala.concurrent.duration._
import spray.http._
import spray.can.Http
import akka.io.IO
import scala.concurrent.{ Await, Future }
import akka.pattern.ask
import HttpMethods._
import spray.util._
import spray.http.HttpRequest
import spray.http.HttpResponse
import scala.Some
import spray.http.HttpHeaders.{ RawHeader, `Content-Type` }
import spray.httpx.marshalling.Marshaller
import org.json4s.JsonFormat
import spray.httpx.SprayJsonSupport
import scala.util.control.NonFatal
import ContentTypes._

trait RestOperations {

  def get(url: String, headers: Option[List[HttpHeader]], queryParams: Option[Map[String, String]]): HttpResponse
  def post[T: Marshaller](url: String, payload: T, headers: Option[List[HttpHeader]], queryParams: Option[Map[String, String]]): HttpResponse
  def put[T: Marshaller](url: String, payload: T, headers: List[HttpHeader], queryParams: Option[Map[String, String]]): HttpResponse
  def delete(url: String, headers: Option[List[HttpHeader]], queryParams: Option[Map[String, String]]): HttpResponse
  def postFormData(url: String, headers: List[HttpHeader], fields: Seq[(String, String)], queryParams: Option[Map[String, String]] = None): HttpResponse
}

object RestClient {

  StatusCodes.registerCustom(596, "Service Not Found", "Service Not Found")

  private val defaultActorSystemName = "Platform-Api-Automation"

  def apply(): RestClient = apply(defaultActorSystemName)

  def apply(name: String): RestClient = new RestClient(name)

}

class RestClient(val actorSystemName: String) extends RestOperations {

  private implicit val timeout: Timeout = 60.seconds
  //  maximum wait time this current actor blocks for the response to arrive.
  private val atMostWaitTime = 60.seconds

  implicit val system = ActorSystem(actorSystemName)

  import system.dispatcher

  val log = Logging(system, getClass)

  val pipeline = sendReceive

  import SprayJsonSupport._

  override def get(url: String, headers: Option[List[HttpHeader]], queryParams: Option[Map[String, String]] = None): HttpResponse = {
    doHttp(Get(Uri(url).withQuery(queryParams.getOrElse(Map()))).withHeaders(headers.getOrElse(List())))
  }

  override def post[T: Marshaller](url: String, payload: T, headers: Option[List[HttpHeader]], queryParams: Option[Map[String, String]] = None): HttpResponse = {

    doHttp(Post(Uri(url).withQuery(queryParams.getOrElse(Map())), payload).withHeaders(headers.getOrElse(List())))
  }

  implicit val intMarshaller = Marshaller.of[Int](`application/json`) {

    (value, ct, ctx) => ctx.marshalTo(HttpEntity(ct, s"{ value: $value }"))
  }

  def postData(url: String, payload: String, headers: Option[List[HttpHeader]], queryParams: Option[Map[String, String]] = None): HttpResponse = {

    doHttp(Post(Uri(url).withQuery(queryParams.getOrElse(Map()))).withHeaders(headers.getOrElse(List())).withEntity(HttpEntity(`application/json`, payload))) //post with request parameters as "content" directive
  }

  def putData(url: String, payload: String, headers: Option[List[HttpHeader]], queryParams: Option[Map[String, String]] = None): HttpResponse = {

    doHttp(Put(Uri(url).withQuery(queryParams.getOrElse(Map()))).withHeaders(headers.getOrElse(List())).withEntity(HttpEntity(`application/json`, payload)))
  }

  override def postFormData(url: String, headers: List[HttpHeader], fields: Seq[(String, String)], queryParams: Option[Map[String, String]] = None): HttpResponse = {
    doHttp(Post(Uri(url).withQuery(queryParams.getOrElse(Map())), FormData(fields)).withHeaders(headers))
  }

  override def put[T: Marshaller](url: String, payload: T, headers: List[HttpHeader], queryParams: Option[Map[String, String]] = None): HttpResponse = {
    doHttp(Put(Uri(url).withQuery(queryParams.getOrElse(Map())), payload).withHeaders(headers))
  }

  def doHttp(message: HttpRequest): HttpResponse = {
   println(message)
    if (log.isDebugEnabled) log.debug("[ HttpRequest : %s".format(message))
    var result = None: Option[HttpResponse]
    try {
      val actorRef = for {
        response <- IO(Http).ask(message).mapTo[HttpResponse]

      } yield {
        if (log.isDebugEnabled)
          log.debug("[ HttpResponse : %s".format(response))
        else
          log.info("Request-Level API: received response with status {}", response.status)
        result = Some(response)
      }
      Await.result(actorRef, atMostWaitTime)
    } catch {
      case e: Throwable => {
        log.error(e, "[ Error in making the the call [HttpRequest : %s ]]".format(message))
        throw e
      }

    }
    if (result.get.status.intValue == 504)
      doHttp(message)
    else result.get

  }

  override def delete(url: String, headers: Option[List[HttpHeader]] = None, queryParams: Option[Map[String, String]] = None): HttpResponse = {
    doHttp(Delete(Uri(url).withQuery(queryParams.getOrElse(Map()))).withHeaders(headers.getOrElse(List())))
  }

  def shutdown(): Unit = {
    IO(Http).ask(Http.CloseAll)(1.second).await
    system.shutdown()
  }

}