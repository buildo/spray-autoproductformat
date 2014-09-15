organization  := "io.buildo"

name := "spray-autoproductformat"

version       := "0.1-SNAPSHOT"

scalaVersion  := "2.11.0"

scalacOptions := Seq("-unchecked",
                     "-deprecation",
                     "-feature",
                     "-encoding", "utf8")

libraryDependencies ++= Seq(
  "io.buildo"           %%  "ingredients-logging" % "0.1-SNAPSHOT",
  "io.spray"            %%  "spray-json"      % "1.2.6",
  "org.scala-lang"      %   "scala-reflect"   % "2.11.0"
)

publishTo := Some(Resolver.file("file", new File("releases")))
