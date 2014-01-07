name := "tinkerpop-play"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.tinkerpop.blueprints" % "blueprints-orient-graph" % "2.3.0",
  "com.orientechnologies" % "orientdb-client" % "1.3.0",
  "com.orientechnologies" % "orientdb-enterprise" % "1.3.0",
  "com.tinkerpop.blueprints" % "blueprints-core" % "2.3.0",
  "com.tinkerpop" % "frames" % "2.3.0"
)

play.Project.playScalaSettings
