
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

logBuffered in Test := false

lazy val commonSettings = Seq(
  organization := "ru.twistedlogic",
  organizationName := "Twistedlogic",
  organizationHomepage := Some(new URL("http://twistedlogic.ru/")),
  version := "0.0.11",
  scalaVersion := "2.12.8",
  licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  scalacOptions ++= Seq(
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
    "-Ywarn-unused-import"
  )
)

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    name := "pbson",
    homepage := Some(url("https://evgenekiiski.github.io/pbson/")),
    description := "Scala bson library",
    libraryDependencies ++= Seq(
      "org.mongodb" % "bson" % "3.10.1",
      "com.chuusai" %% "shapeless" % "2.3.3",
      "org.mongodb.scala" %% "mongo-scala-bson" % "2.6.0" % Test,
      "org.scalactic" %% "scalactic" % "3.0.5" % Test,
      "org.scalatest" %% "scalatest" % "3.0.5" % Test,
      "org.scalacheck" %% "scalacheck" % "1.14.0" % Test,
      "org.typelevel" %% "cats-laws" % "1.5.0" % Test,
      "org.typelevel" %% "discipline" % "0.10.0" % Test
    ),
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
      "org.typelevel" %% "discipline" % "0.10.0" % Test,
      "org.scalactic" %% "scalactic" % "3.0.5" % Test,
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    )
  )

lazy val benchmarks = (project in file("benchmarks"))
  .enablePlugins(JmhPlugin)
  .dependsOn(root, examples)
  .settings(
    commonSettings,
    name := "benchmarks",
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.10.0",
      "io.circe" %% "circe-generic" % "0.10.0",
      "io.circe" %% "circe-parser" % "0.10.0",
      "org.mongodb.scala" %% "mongo-scala-driver" % "2.6.0",
      "junit" % "junit" % "4.12" % Test,
      "org.typelevel" %% "discipline" % "0.10.0" % Test,
      "org.scalactic" %% "scalactic" % "3.0.5",
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    )
  )








