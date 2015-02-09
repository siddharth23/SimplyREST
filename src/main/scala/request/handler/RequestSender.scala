package request.handler
import configs.RestClient
import spray.http.HttpHeaders.RawHeader
object RequestSender extends App with JsonData {
  val rest = new RestClient("RestClient")
  for (i <- 1 to noOfGetRequests) {
    val response = rest.get(url, Some(headers), Some(params))
    println("\nReceived response with status -  " + response.status.intValue + "\n")
    println(response.entity.asString)
  }
  for (i <- 1 to noOfPostRequests) {
    val response = rest.postData(url, postData, Some(headers))
    println("\nReceived response with status -  " + response.status.intValue + "\n")
    println(response.entity.asString)
  }
  for (i <- 1 to noOfPutRequests) {
    val response = rest.putData(url, putData, Some(headers))
    println("\nReceived response with status -  " + response.status.intValue + "\n")
    println(response.entity.asString)
  }
  for (i <- 1 to noOfDeleteRequests) {
    val response = rest.delete(url, Some(headers))
    println("\nReceived response with status -  " + response.status.intValue + "\n")
    println(response.entity.asString)
  }
  rest.shutdown()
}