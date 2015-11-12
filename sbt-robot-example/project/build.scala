import com.mojolly.scalate.ScalatePlugin.ScalateKeys._
import com.mojolly.scalate.ScalatePlugin._
import net.codejitsu.robot.SbtRobot._
import net.codejitsu.tasks.dsl.{Verbose, FullOutput}
import org.scalatra.sbt._
import sbt.Keys._
import sbt._
import DeploymentTasks._

object SbtRobotDeployExample extends Build {
  val Organization = "net.codejitsu"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.11.6"
  val ScalatraVersion = "2.4.0.RC1"

  val infoFiles = taskKey[Seq[File]]("")

  lazy val project = Project(
    "sbt-robot-example",
    file("."),
    settings = ScalatraPlugin.scalatraSettings ++ scalateSettings ++ Seq(
      organization := Organization,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
      libraryDependencies ++= Seq(
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
        "ch.qos.logback" % "logback-classic" % "1.1.3" % "runtime",
        "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
        "net.codejitsu" % "tasks-dsl_2.11" % "0.0.3-SNAPSHOT"
      ),
      scalateTemplateConfig in Compile <<= (sourceDirectory in Compile) { base =>
        Seq(
          TemplateConfig(
            base / "webapp" / "WEB-INF" / "templates",
            Seq.empty, /* default imports should be added here */
            Seq(
              Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true)
            ), /* add extra bindings here */
            Some("templates")
          )
        )
      },
      infoFiles := {
        val base = (baseDirectory in Compile).value
        val file = base / "project" / "BuildInfo.scala"
        val content = "package projectbuildinfo\n\n" +
          "object BuildInfo {\n" +
          "  val name = \"" + name.value + "\"\n" +
          "  val version = \"" + version.value + "\"\n" +
          "  val artifact = \"" + (artifactPath in (Compile, packageBin)).value.getPath.dropRight(3) + "war" + "\"\n" +
          "  val artifactName = \"" + (artifactPath in (Compile, packageBin)).value.getName.dropRight(3) + "war" + "\"\n" +
          "  val context = \"" + (artifactPath in (Compile, packageBin)).value.getName.dropRight(4) + "\"\n" +
          "}\n"
        IO.write(file, content)
        Seq(file)
      },
      sourceGenerators in Compile <+= infoFiles,
      defineTask(deployApp, "deploy", "deploy app", Verbose),
      defineTask(showScalaStackOverflow, "soScala", "Get last StackOverflow.com Scala posts", FullOutput),
      defineTask(showSbtStackOverflow, "soSbt", "Get last StackOverflow.com SBT posts", FullOutput)
    )
  )
}
