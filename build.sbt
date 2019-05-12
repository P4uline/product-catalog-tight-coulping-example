name := """play-java-starter-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.11.12", "2.12.4")

libraryDependencies += guice
libraryDependencies += evolutions

// Test Database
libraryDependencies += "com.h2database" % "h2" % "1.4.197"
libraryDependencies += "org.avaje.ebeanorm" % "avaje-ebeanorm-elastic" % "1.1.4"
libraryDependencies ++= Seq("org.apache.commons" % "commons-lang3" % "3.5")
libraryDependencies ++= Seq("com.univocity" % "univocity-parsers" % "2.4.1")

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test
// https://mvnrepository.com/artifact/org.mockito/mockito-all
libraryDependencies += "org.mockito" % "mockito-all" % "1.9.5" % Test


// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))
