
## Quick Start

If you're using SBT, add the following line to your build file:

```scala
resolvers += "JCenter" at "https://jcenter.bintray.com/"
libraryDependencies += "ru.twistedlogic" %% "pbson" % "0.0.6"
```

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
// { "a" : 3, "b" : "45", "id" : "000" }
println(bson.fromBson[TestCase])
// Right(TestCase(3,Some(45),MyId(000)))
```
