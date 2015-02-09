package request.handler
import config.JsonFile.{ dataFromJson, defaultFieldFetcher }
import spray.http.HttpHeaders.RawHeader
trait JsonData {
  val url = defaultFieldFetcher("url")
  val noOfGetRequests = defaultFieldFetcher("NoOfGETCalls").toInt
  val noOfPostRequests = defaultFieldFetcher("NoOfPOSTCalls").toInt
  val noOfPutRequests = defaultFieldFetcher("NoOfPUTCalls").toInt
  val noOfDeleteRequests = defaultFieldFetcher("NoOfDeleteCalls").toInt
  val headersArray = dataFromJson("Suite", "Headers").map { f => f.split(",") }
  val queryParamsArray = dataFromJson("Suite", "QueryParams").map { f => f.split(",") }
  val headersHelper = headersArray.map { x => x.map { f => f.replace("\"", "").replace("[{", "").replace("}]", "").split(":") } }
  val headerHelper = headersHelper.map { x => x.map { f => RawHeader(f(0), f(1)) } }
  val headHelper = headerHelper.map(f => f.toList)
  val headers = headHelper(0)
  val param = queryParamsArray.map { x => x.map { f => f.replace("\"", "").replace("[{", "").replace("}]", "").split(":") } }
  val paramHelper = param.map { x => x.map { f => f(0) -> f(1) } }
  val paramsHelper = paramHelper.map(f => f.toMap)
  val params = paramsHelper(0)
  val postData = dataFromJson("Suite", "Post")(0)
  val putData = dataFromJson("Suite", "Put")(0)
}