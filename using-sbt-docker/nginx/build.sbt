val dest = "/var/lib/nginx/html"

dockerfile in docker := new Dockerfile {
  from("alpine")
  runRaw("""
         |apk update &&
         |apk add nginx &&
	       |echo "daemon off;" >>/etc/nginx/nginx.conf &&
	       |mkdir /run/nginx
         """.stripMargin.replaceAll("[\\n\\r]", ""))
  copy(baseDirectory.value.getParentFile/"index.html", s"$dest/index.html")
  cmdRaw("/usr/sbin/nginx")
}

name := "nginx"
organization := "devops4scala"
version := "0.1"
imageNames in docker := Seq(ImageName(s"${organization.value}/${name.value}:${version.value}"))
