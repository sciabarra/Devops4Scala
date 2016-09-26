name := "website"

organization := "devops4scala"

version := "0.1"

imageNames in docker := Seq(ImageName(s"${organization.value}/${name.value}:${version.value}"))

paradoxTheme := Some(builtinParadoxTheme("generic"))

lazy val nginx = project.in(file("..")/"nginx").enablePlugins(DockerPlugin)

val dest = "/var/lib/nginx/html"

dockerfile in docker := new Dockerfile {
    from((docker in nginx).value.toString)
    copy((paradox in Compile).value, dest)
}
