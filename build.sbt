import sbt.*
import sbt.Keys.*

import xerial.sbt.Sonatype.sonatypeCentralHost
import scala.concurrent.duration.*

resolvers += Resolver.mavenLocal

ThisBuild / sonatypeCredentialHost := sonatypeCentralHost

ThisBuild / organization := "com.fastscala"
ThisBuild / version := "0.0.5"
ThisBuild / scalaVersion := "3.5.2"

ThisBuild / shellPrompt := { state => Project.extract(state).currentRef.project + "> " }

ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
ThisBuild / scalacOptions += "-Wunused:imports"

addCommandAlias(
  "styleCheck",
  "scalafmtSbtCheck; scalafmtCheckAll; compile; scalafixAll --check",
)
addCommandAlias(
  "styleFix",
  "compile; scalafixAll; scalafmtSbt; scalafmtAll",
)

lazy val commonSettings = Seq(
  organization := "com.fastscala",
  sonatypeCentralDeploymentName := s"${organization.value}.${name.value}-${version.value}",
  sonatypeCredentialHost := Sonatype.sonatypeCentralHost,
  sonatypeProfileName := "com.fastscala",
  publishMavenStyle := true,
  licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  homepage := Some(url("https://www.fastscala.com/")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/mizerthilez/fastscala"),
      "scm:git@github.com:mizerthilez/fastscala.git",
    )
  ),
  developers := List(
    Developer(
      id = "david",
      name = "David Miguel Antunes",
      email = "davidmiguel@antunes.net",
      url = url("https://www.linkedin.com/in/david-miguel-antunes/"),
    )
  ),
  // isSnapshot := false,
  publishTo := sonatypePublishToBundle.value,
)

val FSRoot = "./"

lazy val root = (project in file(".")).aggregate(
  fs_core,
  fs_circe,
  fs_scala_xml,
  fs_db,
  fs_components,
)

lazy val fs_core = (project in file(FSRoot + "fs-core"))
  .settings(
    commonSettings,
    name := "fs-core",
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.5.12",
      // "net.logstash.logback" % "logstash-logback-encoder" % "8.0",
      "org.slf4j" % "slf4j-api" % "2.0.16",
      "com.github.loki4j" % "loki-logback-appender" % "1.5.2",
      "io.prometheus" % "prometheus-metrics-core" % "1.3.2",
      "io.prometheus" % "prometheus-metrics-instrumentation-jvm" % "1.3.2",
      "io.prometheus" % "prometheus-metrics-exporter-httpserver" % "1.3.2",
      "com.typesafe" % "config" % "1.4.3",
      "it.unimi.dsi" % "dsiutils" % "2.7.3",
      "org.apache.commons" % "commons-text" % "1.12.0",
      "org.eclipse.jetty" % "jetty-server" % "12.0.14",
      "org.eclipse.jetty.websocket" % "jetty-websocket-jetty-server" % "12.0.14",
    ),
  )

lazy val fs_circe = (project in file(FSRoot + "fs-circe"))
  .settings(
    commonSettings,
    name := "fs-circe",
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.14.10",
      "io.circe" %% "circe-generic" % "0.14.10",
      "io.circe" %% "circe-parser" % "0.14.10",
    ),
  )
  .dependsOn(fs_core)

lazy val fs_scala_xml = (project in file(FSRoot + "fs-scala-xml"))
  .settings(
    commonSettings,
    name := "fs-scala-xml",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-xml" % "2.3.0"
    ),
  )
  .dependsOn(fs_core)

lazy val fs_db = (project in file(FSRoot + "fs-db"))
  .settings(
    commonSettings,
    name := "fs-db",
    libraryDependencies ++= Seq(
      "org.postgresql" % "postgresql" % "42.7.4",
      "org.xerial" % "sqlite-jdbc" % "3.47.0.0",
      "org.scalikejdbc" %% "scalikejdbc" % "4.3.2",
      "com.google.guava" % "guava" % "33.3.1-jre",
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    ),
    Test / parallelExecution := false,
  )
  .dependsOn(fs_core)
  .dependsOn(fs_circe)
  .dependsOn(fs_scala_xml)

lazy val fs_components = (project in file(FSRoot + "fs-components"))
  .settings(
    commonSettings,
    name := "fs-components",
    scalacOptions ++= Seq("-Xmax-inlines", "64"),
    libraryDependencies ++= Seq(
      "joda-time" % "joda-time" % "2.13.0"
    ),
  )
  .dependsOn(fs_core)
  .dependsOn(fs_circe)
  .dependsOn(fs_scala_xml)

lazy val fs_demo = (project in file(FSRoot + "fs-demo"))
  .enablePlugins(JavaServerAppPackaging, SystemdPlugin)
  .settings(
    name := "fs-demo",
    Compile / packageBin / mainClass := Some("com.fastscala.demo.server.JettyServer"),
    Compile / mainClass := Some("com.fastscala.demo.server.JettyServer"),
    Compile / unmanagedResourceDirectories += baseDirectory.value / "src" / "main" / "scala",
    publishArtifact := true,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.5.5",
      "at.favre.lib" % "bcrypt" % "0.10.2",
      "com.lihaoyi" %% "scalatags" % "0.13.1",
    ),
    bashScriptEnvConfigLocation := Some("/etc/default/" + (Linux / packageName).value),
    rpmRelease := "1.0.0",
    rpmVendor := "kezlisolutions",
    rpmLicense := Some("none"),
    Linux / daemonUser := "fs_demo",
    Linux / daemonGroup := "fs_demo",
    // javaOptions += "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005",
    Compile / run / fork := true,
    Compile / run / connectInput := true,
    javaOptions += "-Xmx2G",
    javaOptions += "-Xms400M",
  )
  .dependsOn(fs_components)
  .dependsOn(fs_db)

lazy val fs_taskmanager = (project in file(FSRoot + "fs-taskmanager"))
  .enablePlugins(JavaServerAppPackaging, SystemdPlugin)
  .settings(
    name := "fs-taskmanager",
    Compile / packageBin / mainClass := Some("com.fastscala.taskmanager.server.JettyServer"),
    Compile / mainClass := Some("com.fastscala.taskmanager.server.JettyServer"),
    Compile / unmanagedResourceDirectories += baseDirectory.value / "src" / "main" / "scala",
    publishArtifact := true,
    bashScriptEnvConfigLocation := Some("/etc/default/" + (Linux / packageName).value),
    rpmRelease := "1.0.0",
    rpmVendor := "kezlisolutions",
    rpmLicense := Some("none"),
    Linux / daemonUser := "fs_taskmanager",
    Linux / daemonGroup := "fs_taskmanager",
    // javaOptions += "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005",
    Compile / run / fork := true,
    Compile / run / connectInput := true,
    javaOptions += "-Xmx2G",
    javaOptions += "-Xms400M",
  )
  .dependsOn(fs_demo)
