package net.codejitsu.app

import org.scalatra.test.specs2._

class SbtScalatraServletSpec extends ScalatraSpec { def is =
  "GET / on SbtScalatraServlet"                     ^
    "should return status 200"                  ! root200^
                                                end

  addServlet(classOf[SbtScalatraServlet], "/*")

  def root200 = get("/") {
    status must_== 200
  }
}
