lazy val jdk1 = project.in(file("jdk1")).
 enablePlugins(MosaicoDockerPlugin)

 lazy val jdk2 = project.in(file("jdk2")).
  enablePlugins(MosaicoDockerPlugin)

lazy val jdk3 = project.in(file("jdk3")).
    enablePlugins(MosaicoDockerPlugin,MosaicoAmmonitePlugin)
