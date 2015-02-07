name := "SimplyRest"

version := "1.0"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "org.json4s" % "json4s-native_2.11" % "3.2.11",
  "io.spray" % "spray-http_2.11" % "1.3.2-20140909" ,
  "io.spray" % "spray-httpx_2.11" % "1.3.2-20140909" ,
  "io.spray" % "spray-client_2.11" % "1.3.2-20140909" ,
  "io.spray" % "spray-json_2.11" % "1.3.1" ,
  "com.typesafe.akka" %% "akka-actor" % "2.3.7"
)

resolvers ++= Seq(
  "sbt-plugins" at "http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/",
  "spray repo" at "http://repo.spray.io"
)