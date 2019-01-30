
[![Build status](https://img.shields.io/circleci/project/github/EvgeneKiiski/pbson.svg?style=plastic)](https://circleci.com/gh/EvgeneKiiski/pbson/tree/dev)
[![Coverage Status](https://coveralls.io/repos/github/EvgeneKiiski/pbson/badge.svg?branch=dev)](https://coveralls.io/github/EvgeneKiiski/pbson?branch=dev)

# PBson

pbson is a BSON library for Scala.

The goal of this library is to create at compile-time the boilerplate necessary to encode and decode of a certain type.
The pbson provides generic codec derivation using [Shapeless](https://github.com/milessabin/shapeless).    

pbson can derive bson encoder and decoder:

``` BsonEncoder[T] : T => BsonValue ```

``` BsonDecoder[T] : BsonValue => Either[BsonError, T]```

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
// { "a" : 3, "b" : "45", "id" : "000" }
println(bson.fromBson[TestCase])
// Right(TestCase(3,Some(45),MyId(000)))
```

## Getting pbson

The current stable version is 0.0.6

If you're using SBT, add the following line to your build file:

```scala
resolvers += "MParser.org" at "http://repository.mparser.org/"
libraryDependencies += "ru.twistedlogic" %% "pbson" % "0.0.6"
```

## Wrappers (encode and decode as value)
```scala
case class MyId(value: String) extends AnyVal
  
implicit val testCaseEncoder: BsonEncoder[MyId] = deriveEncoder
implicit val testCaseDecoder: BsonDecoder[MyId] = deriveDecoder

val test = MyId("000")

val bson = test.toBson
println(bson)
// BsonString{value='000'}
println(bson.fromBson[MyId])
// Right(MyId(000))
```

## ADT (Algebraic Data Types)

### semiauto
```scala
sealed trait ADT

object ADT {
  case class A(s: String) extends ADT
  case class B(a: Int) extends ADT

  implicit val aEncoder: BsonEncoder[A] = deriveEncoder
  implicit val aDecoder: BsonDecoder[A] = deriveDecoder

  implicit val bEncoder: BsonEncoder[B] = deriveEncoder
  implicit val bDecoder: BsonDecoder[B] = deriveDecoder
}

import ADT._

implicit val adtEncoder: BsonEncoder[ADT] = deriveEncoder
implicit val adtDecoder: BsonDecoder[ADT] = deriveDecoder

val test : ADT = B(4)

val bson = test.toBson
println(bson)
// { "a" : 4, "type" : "B" }
println(bson.fromBson[ADT])
// Right(B(4))
```
### Enum ( as String encoder and decoder)
```scala
sealed trait ADT

object ADT {
  final case object A extends ADT
  final case object B extends ADT
}

import ADT._

implicit val adtEnumEncoder: BsonEncoder[ADT] = asStringEncoder {
  case A => "A"
  case B => "B"
}

implicit val adtEnumDecoder: BsonDecoder[ADT] = asStringDecoder {
  case "A" => A
  case "B" => B
}

val test : ADT = B

val bson = test.toBson
println(bson)
// BsonString{value='B'}
println(bson.fromBson[ADT])
// Right(B)
```

## Map
### semiauto
```scala
case class TestCase(a: Map[String, String])

implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

val test : TestCase = TestCase(Map("key1" -> "value1", "key2" -> "value2"))

val bson = test.toBson
println(bson)
// { "a" : { "key1" : "value1", "key2" : "value2" } }
println(bson.fromBson[TestCase])
// Right(TestCase(Map(key1 -> value1, key2 -> value2)))
```
### map as array
```scala
case class TestCase(a: Map[String, String])

implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

implicit val mapEncoder: BsonEncoder[Map[String, String]] = map2ArrayEncoder
implicit val mapDecoder: BsonDecoder[Map[String, String]] = array2MapDecoder

val test : TestCase = TestCase(Map("key1" -> "value1", "key2" -> "value2"))

val bson = test.toBson
println(bson)
// { "a" : [{ "_v" : "value1", "_k" : "key1" }, { "_v" : "value2", "_k" : "key2" }] }
println(bson.fromBson[TestCase])
// Right(TestCase(Map(key1 -> value1, key2 -> value2)))
```

### map (key, value case classes) as array
```scala
case class Key(k: String)
case class Value(v: String)
case class TestCase(a: Map[Key, Value])

implicit val keyEncoder: BsonEncoder[Key] = deriveEncoder
implicit val keyDecoder: BsonDecoder[Key] = deriveDecoder

implicit val nestedEncoder: BsonEncoder[Value] = deriveEncoder
implicit val nestedDecoder: BsonDecoder[Value] = deriveDecoder

implicit val mapEncoder: BsonEncoder[Map[Key, Value]] = map2ArrayEncoder
implicit val mapDecoder: BsonDecoder[Map[Key, Value]] = array2MapDecoder

implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

val test : TestCase = TestCase(Map(Key("45") -> Value("34"), Key("23") -> Value("45")))

val bson = test.toBson
println(bson)
// { "a" : [{ "v" : "34", "k" : "45" }, { "v" : "45", "k" : "23" }] }
println(bson.fromBson[TestCase])
// Right(TestCase(Map(Key(45) -> Value(34), Key(23) -> Value(45))))
```

## Resources

The [examples](https://github.com/EvgeneKiiski/pbson/blob/master/examples/src/main/scala/pbson/examples/)


