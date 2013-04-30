import AssemblyKeys._

assemblySettings

name := "coverage"

mainClass in (Compile, packageBin) := Some("com.github.mylons.coverage.ComputeCoverageStats")

mainClass in (Compile, run) := Some("com.github.mylons.coverage.ComputeCoverageStats")

jarName in assembly := "coverageStats.jar"

libraryDependencies ++= Seq (
        "com.typesafe" % "scalalogging-log4j_2.10" % "1.0.1",
        "org.apache.logging.log4j" % "log4j-api" % "2.0-beta4",
        "org.apache.logging.log4j" % "log4j-core" % "2.0-beta4",
        "io.spray" %%  "spray-json" % "1.2.3"
)
