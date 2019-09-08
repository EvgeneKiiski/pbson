import sbtcrossproject.{ CrossProject, CrossType }

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

logBuffered in Test := false

autoCompilerPlugins := true

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.0")

lazy val compilerOptions = Seq(
  "-encoding", "utf8",
  "-Xfatal-warnings",
  "-deprecation",
  "-unchecked",
  "-opt:l:inline",
  "-opt-inline-from:pbson.**",
  "-Ypartial-unification",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-Ywarn-unused-import",
)

lazy val commonSettings = Seq(
  organization := "ru.twistedlogic",
  organizationName := "Twistedlogic",
  organizationHomepage := Some(new URL("http://twistedlogic.ru/")),
  version := "0.0.18",
  crossScalaVersions := Seq("2.11.12", "2.12.9", "2.13.0"),
  licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, minor)) if minor == 11 =>
        compilerOptions.flatMap {
          case "-opt:l:inline" => Nil
          case other => Seq(other)
        }
      case Some((2, minor)) if minor == 12 => compilerOptions
      case Some((2, minor)) if minor >= 13 =>
        compilerOptions.flatMap {
          case "-Ywarn-unused-import" => Seq("-Ywarn-unused:imports")
          case "-Ypartial-unification" => Nil
          case "-Xfuture" => Nil
          case other => Seq(other)
        }
      case _ => Nil
    }
  }
)


lazy val root = (project in file("."))
  .settings(
    commonSettings,
    name := "pbson",
    homepage := Some(url("https://evgenekiiski.github.io/pbson/")),
    description := "Scala bson library",
    unmanagedSourceDirectories in Compile ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, minor)) if minor <= 12 => List(baseDirectory.value / "src/main/scala-2.12-/")
        case Some((2, minor)) if minor >= 13 => List(baseDirectory.value / "src/main/scala-2.13+/")
        case _ => Nil
      }
    },
    unmanagedSourceDirectories in Test ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, minor)) if minor <= 12 => List(baseDirectory.value / "src/test/scala-2.12-/")
        case Some((2, minor)) if minor >= 13 => List(baseDirectory.value / "src/test/scala-2.13+/")
        case _ => Nil
      }
    },
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, minor)) if minor <= 12 =>
          Seq(
            "org.mongodb" % "bson" % "3.11.0",
            "com.chuusai" %% "shapeless" % "2.3.3",
            "org.scalactic" %% "scalactic" % "3.0.8" % Test,
            "org.scalatest" %% "scalatest" % "3.0.8" % Test,
            "org.scalacheck" %% "scalacheck" % "1.14.0" % Test,
            "org.typelevel" %% "cats-laws" % "2.0.0-M4" % Test,
            "org.typelevel" %% "discipline" % "0.11.1" % Test
          )
        case Some((2, minor)) if minor >= 13 =>
          Seq(
            "org.mongodb" % "bson" % "3.11.0",
            "com.chuusai" %% "shapeless" % "2.3.3",
            "org.scalactic" %% "scalactic" % "3.0.8" % Test,
            "org.scalatest" %% "scalatest" % "3.0.8" % Test,
            "org.scalacheck" %% "scalacheck" % "1.14.0" % Test,
          )
        case _ => Nil
      }
    },
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/EvgeneKiiski/pbson"),
        "scm:git:git@github.com:EvgeneKiiski/pbson.git"
      )
    ),
    publishTo := Some(Resolver.file("file", new File("repository")))
  )


lazy val examples = (project in file("examples"))
  .dependsOn(root)
  .settings(
    commonSettings,
    name := "examples",
    libraryDependencies ++= Seq(
      "org.mongodb.scala" %% "mongo-scala-driver" % "2.6.0",
      "junit" % "junit" % "4.12" % Test,
      "org.scalactic" %% "scalactic" % "3.0.8" % Test,
      "org.scalatest" %% "scalatest" % "3.0.8" % Test
    )
  )

lazy val compiler = (project in file("compiler"))
  .dependsOn(root)
  .settings(
    commonSettings,
    scalacOptions ++= Seq(
      "-Ystatistics:typer",
    ),
    name := "compiler-tests",
    libraryDependencies ++= Seq(
      "junit" % "junit" % "4.12" % Test,
      "org.scalactic" %% "scalactic" % "3.0.8" % Test,
      "org.scalatest" %% "scalatest" % "3.0.8" % Test
    )
  )

lazy val benchmarks = (project in file("benchmarks"))
  .enablePlugins(JmhPlugin)
  .dependsOn(root, examples)
  .settings(
    commonSettings,
    name := "benchmarks",
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.12.0-M4",
      "io.circe" %% "circe-generic" % "0.12.0-M4",
      "io.circe" %% "circe-parser" % "0.12.0-M4",
      "org.mongodb.scala" %% "mongo-scala-driver" % "2.6.0",
      "junit" % "junit" % "4.12" % Test,
      "org.scalactic" %% "scalactic" % "3.0.8",
      "org.scalatest" %% "scalatest" % "3.0.8" % Test
    )
  )








