title: Devops4Scala: Using SBT to for advanced Docker builds
date: 2016/09/19 20:00:00
tags: ['Devops', 'Docker']

---


I am a Scala developers, and I routinely develop applications I deploy in the cloud. The tool of the choice for packaging cloud applications is, as you can guess, *Docker*. As a result,  I spend a lot of timewriting `Dockerfile`s.

As much I love and appreciate Docker, I actually dislike `Dockerfile`s. I am sure If you ever tried to create Docker images with the standard `Dockerfile` syntax, you may share my feelings the are too limited. To be honest, I believe those limitations are in a sense a good thing, because they impose Docker to be a simple and well understood tool with a clear scope and well defined goals.

However in real world, builds are complex enough you need *much more* than `Dockerfiles` provides out-of-the-box.   In this post I will explore how I overcome some limitations using the mighty Scala build tools [SBT](http://www.scala-sbt.org) with the [`sbt-docker`](https://github.com/marcuslonnberg/sbt-docker) plugin. I also added in the mix the awesome Scala scripting shell [Ammonite](http://www.lihaoyi.com/Ammonite/) by Li Haoyi.

Using those tools I am building  a Devops solution for scala applications, I call [Mosaico](https://github.com/sciabarra/Mosaico) now at version 0.2, which includes a plugin `sbt-mosaico` and a set of docker images that you can reuse in your application.

If you are a Scala developer interested in super powering your Docker builds, read on.

<!-- more -->

`Dockerfile`s are really meant to create reusable (and somewhat immutable) components to be shared on public services like the [Docker Hub](http://hub.docker.com). The real intent of this format is to be able to create repeatable builds that can be used by anyone everywhere.

By design, it assumes all you need to build your image either exists in the source directory or it can be downloaded straight away from the internet. Furthermore, they restrict you to use only linux scripting tools (`bash` mostly) to do all the work.

I use a lot Docker, so often I can write a `Dockerfile` on top of my mind without having to check documentation. So I am very familiar with what you can and cannot do (a lot) with them. Here is a list of the a list the requirements I usually have with `Dockerfiles` when I build my images:

- I need to express *dependencies* between images. I may have a base container and then a number of derived containers built from a base container. And you need to update the base container before you actually build the derived one, without having to do it manually.

- I need to collect artifacts from different sources, not just your build folder. `Dockerfiles` are adamant in the fact everything should be in the source directory. As a result, you need to copy your stuff in the source directory manually.

- I need to be able to download something from Internet,  but avoid to repeat the download all the time (as `Dockerfile` usually do) while developing your build, to avoid getting old while the stuff is downloaded and downloaded again even if it is still there.

- I need  to be able to add something in your container compiling it. But I need this without having to add a whole new development environment in your container then removing it after you have compiled your stuff.

You can gather all those requirements in a simple statement: you need a build tools to be run before you actually build your Docker images.  

Luckily, Scla SBT is such a build tool and here I show how you can do advanced builds with SBT.
https://github.com/marcuslonnberg/sbt-docker
# Writing your `Dockerfile` with `sbt-docker`

The starting point of my effort is the plugin [`sbt-docker`](https://github.com/marcuslonnberg/sbt-docker), a really smart SBT plugin by Marcus Lonnberg. This plugin lets you to describe the whole `Dockerfile` in sbt syntax. The advantage of this technique lies in getting the whole power of SBT at your fingertips, being no more limited to what `Dockerfile` has to offer.

The obvious disadvantage is of course you need to learn SBT, but hey, this article is part of the Devops4Scala series, and it is aimed to those Scala developers who know Scala and SBT, so  it should not be an issue.

Let's start learning `sbt-docker` first building an image for a web server based on `nginx` in the classic way, and then rewriting it in `SBT` syntax highlighting the advantages.

## A classic `Dockerfile` for `nginx`

Without any further ado here is our example:

```
FROM alpine
RUN apk update &&\
    apk add nginx &&\
	  echo "daemon off;" >>/etc/nginx/nginx.conf &&\
	  mkdir /run/nginx
COPY index.html /var/lib/nginx/html/index.html
CMD /usr/sbin/nginx
```

We start from the an `alpine` linux distribution image (very commonly used with `Docker`), we add  and configure `nginx`, copy a file in the image and start `nginx`. That is almost all.

However, in the `Dockerfile` we do not specify the name of the image. And the file `index.html` is not acutally stored in the same folder of the `Dockerfile`, but in its parent directory. As a result we need (and commonly we have) a build script like this one:

```
cp index.html nginx-classic/index.html
docker build -t devops4scala/nginx-classic:1 nginx-classic
```

The script is simple but it shows a few  problems already>

- you need additional scripts to collect things and tag images properly
- no way to parametrize the Dockerfile
- error prone way just to write a long command line (just a space after the final backslash breaks the script)

## Preparing to use `SBT` and `sbt-docker`

Now let's see how to improve with the help of Scala tools.

Before starting you need to install [SBT](http://www.scala-sbt.org). Follow installation instructions,
SBT is available for almost every operationg system with multiple installation options.

Once you have installed SBT you need to enable the `sbt-plugin` for your project.
To do so, create a folder for your project and place in it 2 files.

First is the file `project/plugins.sbt`  (yes, you need a `project` subfolder) with the single line:

```
addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "1.4.0")
```

Second, the file `plugins.sbt` (in the folder you created) with the single line:

```
enablePlugins(DockerPlugin)
```

You are ready.

## A `Dockerfile` generated in `sbt`

Now let's create the equivalent `Dockerfile` with a `build.sbt` as follows:

```
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
```

As long you know `Dockerfile` syntax and Scala syntax, you should notice that `sbt-docker` looks like a DSL expressing the Docker build file in Scala syntax. But that is only the beginning because you can see a number of advantages.

We can immediately notice a few improvements.

For clarity, destination folder in the container is declared in a variable (`val dest`) at the beginning of the file.
The variable is used later using Scala string interpolation syntax (`s"$dest/index.html"`).

We can actually do better than this placing those variables in a separate file, and I will describe in another post how to do this. For now just note you have got parametrization (and there are entire packages for Docker just offering you this)

We can actually do more from this. Note the long string in the `runRaw` command. We do not have to worry of avoiding to terminate and we can enjoy indentation as well as a final stripping of all the newlines applying  the expression `.stripMargin.replaceAll("[\\n\\r]", "")`

Furthermore you can note the image name is in the same file, and the name of the image is calculated from other settings (actually the standards SBT settings used to identify the package we compile).

Last but not least: we do not need to copy files anymore in the folder: the source file for the index is specified as `baseDirectory.value.getParentFile` and it is copied from the parent directory instead of having to do it in our build script.

Basically, we replaced the manual build script with an all-in-one build handled by our favorite build tool.

We have covered the basics, now it is time to do more.

## Mdelling dependencies



## But wait! There is (much) more...

Ok for now I stop here but I have not finished. We have still to see how to configure with shared property files, how to download files intelligently, how to use images to build artifacts , how to use Ammonite Scala scripts in builds...

All of this is related to only to Docker builds, there is also how to build images in Amazon, deploy Kubernetes, use Jenkins in the cloud to automate builds and so on.

Stay tuned for more installments of Devops4scala series of blog posts.
