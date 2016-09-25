lazy val nginx = project.in(file("nginx"))
  .enablePlugins(DockerPlugin)

lazy val website = project.in(file("website"))
.enablePlugins(DockerPlugin,ParadoxPlugin)

addCommandAlias("buildAll", "website/docker")
