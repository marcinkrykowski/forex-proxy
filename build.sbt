lazy val commonSettings = Seq(
  name := "forex-proxy",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.12.8",
  scalacOptions ++= Seq(
    "-deprecation",
    "-Xfatal-warnings",
    "-Ywarn-value-discard",
    "-Xlint:missing-interpolator"
  )
)

val SttpVersion = "1.7.2"
val Http4sVersion = "0.21.22"
val PureConfigVersion = "0.12.3"
val TypesafeConfigVersion = "1.4.0"
val Slf4jVersion = "1.0.1"
val LogbackVersion = "1.2.3"
val ScalaMockVersion = "4.4.0"

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    Defaults.itSettings,
    libraryDependencies ++= Seq(
      "org.scalactic" %% "scalactic" % "3.2.0",
      "org.scalatest" %% "scalatest" % "3.2.0" % Test,
      "com.softwaremill.sttp" %% "core" % SttpVersion,
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "com.github.pureconfig" %% "pureconfig" % PureConfigVersion,
      "com.github.pureconfig" %% "pureconfig-cats-effect" % PureConfigVersion,
      "com.typesafe" % "config" % TypesafeConfigVersion,
      "io.chrisdavenport" %% "log4cats-slf4j" % Slf4jVersion,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "org.scalamock" %% "scalamock" % ScalaMockVersion % "test"
    )
  )