
scalaVersion := "2.12.6"

name := "quiz-game"
version := "1.0"

libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.1"
libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.144-R12"
libraryDependencies += "com.typesafe" % "config" % "1.3.2"
libraryDependencies += "org.scalaj" %% "scalaj-http" % "2.4.0"
libraryDependencies += "io.spray" %%  "spray-json" % "1.3.3"
libraryDependencies += "org.apache.commons" % "commons-text" % "1.4"

scalacOptions ++= Seq(
  "-deprecation"
)

