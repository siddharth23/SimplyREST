package config
import scala.io.Source
import java.io.File
import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods.{ parse, render, compact, pretty }
import org.json4s.JsonAST.JString
import java.io.FileWriter
import org.json4s.JsonAST.{ JObject, JArray }
import org.json4s.JsonAST.JValue
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.{ Date, TimeZone }
object JsonFile {

  def dataFromJson(resourceName: String, str: String, filePath: String = "src/test/resources/data.json"): List[String] = {

    val s = Source.fromFile(new File(filePath))
    val json = parse(s.bufferedReader)
    implicit val formats = DefaultFormats
    val methodData = json \\ resourceName \\ str
    val data = methodData.children.map(f => compact(render(f)))
    val checker = methodData.children(0)
    if (checker.isInstanceOf[JArray])
      data
    else if (data.length == 1 || !checker.isInstanceOf[JObject]) {
      val singleData = List(compact(render(methodData)))
      singleData
    } else data
  }
def defaultFieldFetcher(resource: String, filePath: String = "src/test/resources/data.json"): String = {
    val s = Source.fromFile(new File(filePath))
    val json = parse(s.bufferedReader)
    compact(render(json \\ resource)).replace("\"", "")
  }
  def queryParams(resourceName: String, str: String, filePath: String = "src/test/resources/data.json"): List[String] = {

    val s = Source.fromFile(new File(filePath))
    val json = parse(s.bufferedReader)
    implicit val formats = DefaultFormats
    val methodData = json \\ resourceName \\ str \\ "queryparam"
    val s1 = methodData.children.map(f => f.children.map(f => f.values.toString))
    if (methodData.children.length == 1)
      return List(methodData.children(0).extract[String])
    else {
      val s2 = s1.map(f => f.foldRight("")((a, b) => a.concat(b)))
      s2
    }
  }
}