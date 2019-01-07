# PBson

Pure bson encoder decoder

Decode to ```Either[BsonError, A]``` without throws any exceptions.

## Quick Start

```scala
import pbson._
import pbson.semiauto._

case class MyId(value: String) extends AnyVal

case class TestCase(a: Int, b: Option[String], id: MyId)

implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

val test = TestCase(3, Some("45"), MyId("000"))

val bson = test.toBson
println(bson)
println(bson.fromBson[TestCase])

```


## Getting pbson

The current stable version is 0.0.1

If you're using SBT, add the following line to your build file:

```scala
resolvers += "MParser.org" at "http://repository.mparser.org/"
libraryDependencies += "ru.twistedlogic" %% "pbson" % "0.0.1"
```

## Resources

The [example](https://github.com/EvgeneKiiski/pbson/blob/master/examples/src/main/scala/pbson/examples/FullExample.scala)

