name := "website"

organization := "devops4scala"

version := "0.1"


imageNames in docker := Seq(ImageName(s"${organization.value}/${name.value}:${version.value}"))

scalaVersion := "2.11.6"

scalatex.SbtPlugin.projectSettings

lazy val docs = scalatex.ScalatexReadme(
  projectId = "docs",
  wd = file(""),
  url = "https://github.com/sciabarra/Devops4Scala/tree/master",
  source="Main"
)

lazy val nginx = project.in(file("..")/"nginx").enablePlugins(DockerPlugin)

val dest = "/var/lib/nginx/html"

dockerfile in docker := {
  (run in (docs, Compile)).value
  val src = (target in docs).value / "scalatex"
  new Dockerfile {
    from( (docker in nginx).value.toString)
    copy(src, dest)
  }
}
