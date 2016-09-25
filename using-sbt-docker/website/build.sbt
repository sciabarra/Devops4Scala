name := "website"

organization := "devops4scala"

version := "0.1"

imageNames in docker := Seq(ImageName(s"${organization.value}/${name.value}:${version.value}"))

lazy val nginx = project.in(file("..")/"nginx").enablePlugins(DockerPlugin)

val dest = "/var/lib/nginx/html"

dockerfile in docker := new Dockerfile {
    from((docker in nginx).value.toString)
    copy((paradox in Compile).value, dest)
}

// libraryDependencies in paradox += "ch.qos.logback" % "logback-classic" % "1.1.7"

paradoxTheme := Some(builtinParadoxTheme("generic"))
