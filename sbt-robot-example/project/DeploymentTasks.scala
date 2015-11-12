import java.net.URL

import net.codejitsu.tasks._
import net.codejitsu.tasks.dsl.Tasks._
import net.codejitsu.tasks.dsl.{Dev, FullOutput, Localhost, User}
import projectbuildinfo.BuildInfo._

import scala.concurrent.duration._

object DeploymentTasks {
  val hosts = Localhost

  implicit val user = User.load
  implicit val stage = new Dev

  val webapps = "/Library/Tomcat/webapps"

  val RemoveOldArtifacts = Par ~ RmIfExists(hosts, s"/Users/${user.localName}" / s"$artifactName*")
  val UploadNewArtifact = Par ~ Cp(hosts, artifact, s"/Users/${user.localName}/")
  val StopTomcats = Par ~ ShellScript(hosts, "/Library/Tomcat/bin/shutdown.sh")
  val CleanWebapps = Par ~ RmIfExists(hosts, webapps / s"$name*")
  val CopyNewArtifact = Par ~ Mv(hosts, s"/Users/${user.localName}/$artifactName", webapps)
  val StartTomcats = Par ~ ShellScript(hosts, "/Library/Tomcat/bin/startup.sh")
  val CheckDeployedApps = Par ~ CheckUrl(hosts, s"/$context/hello-meetup", 8080, _.contains("Hello, Munich Scala Meetup!"))

  val deployApp =
      RemoveOldArtifacts andThen
      UploadNewArtifact andThen
      StopTomcats andThen
      CleanWebapps andThen
      CopyNewArtifact andThen
      StartTomcats andThen
      Wait(5 seconds) andThen
      CheckDeployedApps

  val getSoScalaData =
    Download(Localhost, new URL("http://stackoverflow.com/feeds/tag?tagnames=scala&sort=newest"),
      params = List("-q", "-O-"))

  val getSoSbtData =
    Download(Localhost, new URL("http://stackoverflow.com/feeds/tag?tagnames=sbt&sort=newest"),
      params = List("-q", "-O-"))

  val getRawSOTitles = Grep(Localhost, params = List("-o"), pattern = Option("<title type=\"text\">[^<]*"))

  val getSOTitles = Grep(Localhost, params = List("-o"), pattern = Option("[^>]*$"), verbose = FullOutput)

  val showScalaStackOverflow =
    getSoScalaData pipeTo
    getRawSOTitles pipeTo
    getSOTitles

  val showSbtStackOverflow =
    getSoSbtData pipeTo
    getRawSOTitles pipeTo
    getSOTitles
}
