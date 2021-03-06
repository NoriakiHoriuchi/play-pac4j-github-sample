name := """play-pac4j-github-sample"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.pac4j" % "play-pac4j" % "2.6.1",
  "org.pac4j" % "pac4j-oauth" % "1.9.5",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

