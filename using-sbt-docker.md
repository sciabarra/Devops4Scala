title: Devops4Scala: Using scala SBT for advanced Docker builds
date: 2016-09-21 10:00:00
tags:
- Docker
- Devops
---

I am a Scala developers, and I develop applications I deploy in the cloud. The tool of choice for packaging cloud applications is Docker. As a result  I spent a lot of time building and packaging  writing `Dockerfile`s.


I am sure If you ever tried to create Docker images with the standard `Dockerfile` syntax, you may have faced the limitations it imposes. To be honest, those limitations are a good thing because force Docker to be a simple and well understood tool with a clear scope and well defined goals.

However in real world, builds are complex enough you need *much more* than Dockerfiles provides out-of-the-box.   In this post I will explore how I overcome some limitations using the mighty Scala build tools [SBT](http://www.scala-sbt.org) with the [`sbt-docker`](https://github.com/marcuslonnberg/sbt-docker) plugin, and the awesome Scala scripting shell [Ammonite](http://www.lihaoyi.com/Ammonite/) by Li Haoyi.

Using those tools I am building  a Devops solution for scala applications, I call [Mosaico](https://github.com/sciabarra/Mosaico) now at version 0.2, which includes a plugin `sbt-mosaico` and a set of docker images that you can reuse.

If you are a Scala developer interested in super powering your Docker builds, read on.

<!-- more -->

I use a lot Docker, so often I can write a Dockerfile on top of my mind without having to check references. As much I love docker, the same I dislike `Dockerfiles`.

`Dockerfile` are really meant to create reusable (and somewhat immutable) components to be shared on public services like the [Docker Hub](http://hub.docker.com).

The real intent of this format is to be able to create repeatable builds that can be used on public internet services like Docker Hub.

By design, it assumes all you need to build your image either exists in the source directory or it can be downloaded from the internet.

Docker however is also used a lot today to build in-house images, not meant to be shared outside of your private environment. My experience with docker showed a number of limitations that makes sense for public hub but not for private images.

Here is a list of the a list the requirements I usually face with `Dockerfiles` when I build private images:

- you need to express *dependencies* between images. You may have a base container and then a number of derived containers built from a base container. And you need to update the base container before you actually build the derived one, without having to do it manually.

- you need to collect artifacts from different sources, not just your build folder. `Dockerfiles` are adamant in the fact everything should be in the source directory. As a result, you need to copy your stuff in the source directory manually.

- you need to be able to download something from Internet,  but avoid to repeat the download all the time (as `Dockerfile` usually do) while developing your build, to avoid getting old while the stuff is downloaded and downloaded again even if it is still there.

- you may need  to be able to add something in your container compiling it but without having to add a whole new development environment in your container then removing it after you have compiled your stuff.

You can gather all those requirements in a simple statement: you need a build tools to be run before you actually build your Docker images.  

Luckily, SBT is such a build tool and here I show how you can do advanced builds with SBT.

# Writing your `Dockerfile` with `sbt-docker`

The starting point of my effort is the plugin [`sbt-docker`], a really smart SBT PLUGIN by Marcus Lönnberg. The basic idea is to generate the whole dockerfile in sbt syntax. The advantage of such a move is get the whole power of SBT at your finger tips and you are no more limited to what `Dockerfile` has to offer.

I will start my explanation building an image for a web server based on `nginx` first in the traditional way then I will show the improvements that are possible using SBT and `sbt-docker`





