lazy val nginx = project.in(file("nginx"))
  .enablePlugins(DockerPlugin)

lazy val website = project.in(file("website"))
.enablePlugins(DockerPlugin)
.settings(scalatex.SbtPlugin.projectSettings : _*)


addCommandAlias("buildAll", "website/docker")
