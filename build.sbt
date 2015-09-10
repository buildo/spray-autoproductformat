organization  := "io.buildo"

name := "spray-autoproductformat"

version       := "0.3.0"

scalaVersion  := "2.11.6"

scalacOptions := Seq("-unchecked",
                     "-deprecation",
                     "-feature",
                     "-encoding", "utf8")

libraryDependencies ++= Seq(
  "io.spray"            %%  "spray-json"      % "1.2.6",
  "org.scala-lang"      %   "scala-reflect"   % "2.11.0",
  "org.scalatest"  %% "scalatest"     % "2.2.0" % "test",
  "org.mockito"    %  "mockito-all"   % "1.9.5" % "test"
)

publishTo := Some(Resolver.file("file", new File("releases")))


