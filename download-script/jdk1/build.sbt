val jdkUrl = "http://download.oracle.com/otn-pub/java/jdk/8u101-b13/jdk-8u101-linux-x64.tar.gz"
val glibcUrl= "https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.23-r3/glibc-2.23-r3.apk"
val dockerName = "devops4scala/alpine-jdk1:1"
val oraCookie = "Cookie: oraclelicense=accept-securebackup-cookie"

imageNames in docker := Seq(ImageName(dockerName))

dockerfile in docker := {
  val base = baseDirectory.value
  new Dockerfile {
    Def.sequential(
      download.toTask(s" $glibcUrl glibc.apk"),
      download.toTask(s" $jdkUrl jdk.tgz $oraCookie")
    ).value
    from("alpine:edge")
    copy(base/"glibc.apk", "/tmp")
    runRaw("apk add --allow-untrusted /tmp/*.apk  && rm /tmp/*.apk")
    add(base/"jdk.tgz", "/usr")
    runRaw("ln -sf /usr/jdk* /usr/java ; chmod +x /usr/java/bin/*")
    env("JAVA_HOME", "/usr/java")
    env("PATH", "/bin:/sbin:/usr/bin:/usr/sbin:/usr/java/bin")
  }
}
