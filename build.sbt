val circeV = "0.13.0"
val http4sV = "0.21.4"
val doobieV = "0.9.0"
val scalaBcryptV = "4.1"
val specs2V = "3.8.9"

lazy val `inventory-tracker` =
  project
    .in(file("inventory-tracker"))
    .enablePlugins(SbtTwirl)
    .enablePlugins(JavaAppPackaging)
    .settings(
      name := "inventory-tracker",
      libraryDependencies ++= Seq(
        "io.circe"              %% "circe-generic"            % circeV,
        "io.circe"              %% "circe-generic-extras"     % circeV,
        "io.circe"              %% "circe-parser"             % circeV,
        "org.tpolecat"          %% "doobie-core"              % doobieV,
        "org.tpolecat"          %% "doobie-postgres"          % doobieV,
        "net.logstash.logback"  %  "logstash-logback-encoder" % "6.3",
        "ch.qos.logback"        %  "logback-classic"          % "1.2.3",
        "org.http4s"            %% "http4s-blaze-client"      % http4sV,
        "org.http4s"            %% "http4s-blaze-server"      % http4sV,
        "org.http4s"            %% "http4s-circe"             % http4sV,
        "org.http4s"            %% "http4s-dsl"               % http4sV,
        "org.http4s"            %% "http4s-twirl"             % http4sV,
        "com.github.t3hnar"     %% "scala-bcrypt"             % scalaBcryptV,
        "org.reactormonk"       %% "cryptobits"               % "1.3",
        "com.typesafe"           % "config"                   % "1.4.0",
        "org.scalacheck"        %% "scalacheck"               % "1.14.3"  % Test,
        "org.scalactic"         %% "scalactic"                % "3.1.2",
        "org.scalatest"         %% "scalatest"                % "3.1.2"   % Test,
        "org.scalatestplus"     %% "scalatestplus-scalacheck" % "3.1.0.0-RC2" % Test
      ),
      Compile / console / scalacOptions ~= ((options: Seq[String]) =>
        options.filterNot(s => s.startsWith("-Ywarn") || s.startsWith("-Xlint")))
    )

lazy val `scaffold-scripts` =
  project
    .in(file("scaffold-scripts"))
    .settings(
      Compile / console / scalacOptions ~= ((options: Seq[String]) =>
        options.filterNot(s => s.startsWith("-Ywarn") || s.startsWith("-Xlint")))
    )

lazy val root = project
  .in(file("."))
  .settings(
    name := "inventory-tracker-root",
    inThisBuild(
      Seq(
        scalaVersion := "2.13.2",
        scalafmtOnCompile := true,
        addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
      )),
  )
  .aggregate(
    `inventory-tracker`
  )
