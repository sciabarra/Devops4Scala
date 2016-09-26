#!/bin/bash
cd /tmp
rm -Rvf Devops4Scala  &>/dev/null
docker kill website   &>/dev/null
docker rm website &>/dev/null
docker rmi -f devops4scala/nginx:0.1 devops4scala/website:0.1  &>/dev/null

function ex {
  echo "\$ $*"
  sleep 1
  $*
}

function msg {
 sleep 1
 cowsay -f tux "$@"
 sleep 1
}

figlet "Devops4Scala"
sleep 2
figlet -f banner "Using sbt-docker"
sleep 2
msg "First, note we do not have any image yet"
ex docker images
msg "Now, let's checkout the kit."
ex git clone https://github.com/sciabarra/Devops4Scala
ex cd Devops4Scala/using-sbt-docker
msg "Time to build the whole thing"
ex sbt buildAll
msg "Let's check what we have got"
ex docker images
msg "Starting and running the image"
ex docker run --name website -p 8000:80 -d devops4scala/website:0.1
msg "Now you can check http://localhost:8000"
