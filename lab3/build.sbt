lazy val commonSettings = Seq(
  name := "lab3",
  version := "0.1",
  scalaVersion := "2.13.1",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % Test,
)


lazy val ordering = project
  .in(file("ordering"))
  .settings(commonSettings)

lazy val bank = project
  .in(file("bank"))
  .settings(commonSettings)

lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .aggregate(ordering, bank)
