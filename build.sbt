import AssemblyKeys._

assemblySettings

//assemblyCacheOutput in assembly := false

organization := "com.github.mylons"

name := "genome-analysis"

scalaVersion in ThisBuild := "2.10.1"

unmanagedBase <<= baseDirectory { base => base / "lib" }

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Twitter repo" at "http://maven.twttr.com"

resolvers += "spray repo" at "http://repo.spray.io"

resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                    "releases"  at "http://oss.sonatype.org/content/repositories/releases")

resolvers += Opts.resolver.sonatypeSnapshots

libraryDependencies ++= Seq (
        "org.specs2" % "specs2_2.10" % "1.14",
        "com.typesafe" % "scalalogging-log4j_2.10" % "1.0.1",
        "org.apache.logging.log4j" % "log4j-api" % "2.0-beta4",
        "org.apache.logging.log4j" % "log4j-core" % "2.0-beta4",
        "io.spray" %%  "spray-json" % "1.2.3" 
)

mainClass in (Compile, packageBin) := Some("com.github.mylons.coverage.ComputeCoverageStats")

mainClass in (Compile, run) := Some("com.github.mylons.coverage.ComputeCoverageStats")

mainClass in assembly := Some("com.github.mylons.coverage.ComputeCoverageStats")

jarName in assembly := "genome-analysis.jar"

