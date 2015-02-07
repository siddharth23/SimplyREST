package cofigs
import spray.http.HttpHeaders.RawHeader
object RequestSender extends App{
val rest=new RestClient("test")
val response=rest.get("http://google.com", Some(List(RawHeader("Content-Type", "application/json"))))
println(response)
}