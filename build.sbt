import spray.revolver.RevolverPlugin.Revolver

organization  := "pl.indykiewicz.devlinks"

version       := "0.1"

scalaVersion  := "2.10.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += "spray repo" at "http://repo.spray.io"

libraryDependencies ++= {
  val akkaVersion = "2.1.4"
  val sprayVersion = "1.1.1"
  Seq(
    "io.spray"            %   "spray-can"     % sprayVersion,
    "io.spray"            %   "spray-routing" % sprayVersion,
    "io.spray"            %%  "spray-json"    % "1.2.6",
    "io.spray"            %   "spray-client"  % sprayVersion,
    "org.json4s"          %% "json4s-native"  % "3.2.9",
    "io.spray"            %   "spray-testkit" % sprayVersion  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaVersion,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaVersion   % "test",
    "org.specs2"          %%  "specs2"        % "2.2.3"       % "test"
  )
}

Revolver.settings
