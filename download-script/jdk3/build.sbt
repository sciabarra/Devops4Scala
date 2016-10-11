prpLookup += baseDirectory.value.getParentFile -> "alpine"

imageNames in docker := Seq(ImageName(prp.value("alpine.jdk3")))

val oraCookie = "Cookie: oraclelicense=accept-securebackup-cookie"


dockerfile in docker := {
  val base = baseDirectory.value
  new Dockerfile {
    Def.sequential(
      download.toTask(s" @glibc.url glibc.apk"),
      download.toTask(s" @jdk.url jdk.tgz $oraCookie"),
      amm.toTask(s" trimjdk")
    ).value
    from("alpine:edge")
    copy(base/"glibc.apk", "/tmp")
    runRaw("apk add --allow-untrusted /tmp/*.apk  && rm /tmp/*.apk")
    add(base/"usr", "/usr")
    runRaw("ln -sf /usr/jdk* /usr/java ; chmod +x /usr/java/bin/*")
    env("JAVA_HOME", "/usr/java")
    env("PATH", "/bin:/sbin:/usr/bin:/usr/sbin:/usr/java/bin")
  }
}
