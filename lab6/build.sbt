lazy val akkaVersion = "2.6.3"

lazy val settings = Seq(
  name := "khinkalnaya-actors",
  version := "1.0",
  scalaVersion := "2.13.1",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
    "org.scalatest" %% "scalatest" % "3.1.0" % Test,
    "com.typesafe" % "config" % "1.4.0"
  )
)

lazy val root = project
  .in(file("."))
  .settings(settings)