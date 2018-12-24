
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

logBuffered in Test := false

lazy val commonSettings = Seq(
  organization := "ru.twistedlogic",
  version := "0.0.1",
  scalaVersion := "2.12.8"
)

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    name := "pbson",
    libraryDependencies ++= Seq(
      "org.mongodb.scala" %% "mongo-scala-bson" % "2.4.0",
      "com.chuusai" %% "shapeless" % "2.3.3",
      "junit" % "junit" % "4.12" % Test,
      "org.typelevel" %% "discipline" % "0.10.0" % Test,
      "org.scalactic" %% "scalactic" % "3.0.5"  % Test,
      "org.scalatest" %% "scalatest" % "3.0.5" % Test
    ),
    publishTo := Some(Resolver.file("file",  new File( "/Users/evg/work/mparser.org/repository" )) )
  )


lazy val examples = (project in file("examples"))
  .dependsOn(root)
  .settings(
    commonSettings,
    name := "examples"
  )








