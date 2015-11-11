package net.codejitsu.app

class SbtScalatraServlet extends SbtScalatraWebAppStack {
  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-meetup">hello to Munich Scala Meetup!</a>
      </body>
    </html>
  }
}
