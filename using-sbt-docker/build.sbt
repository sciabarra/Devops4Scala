lazy val nginx = project.in(file("nginx"))
  .enablePlugins(DockerPlugin)

lazy val website = project.in(file("website"))
.enablePlugins(DockerPlugin)

addCommandAlias("buildAll", "website/docker")
