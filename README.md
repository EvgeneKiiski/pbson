
[![Build status](https://img.shields.io/circleci/project/github/EvgeneKiiski/pbson/master.svg?style=flat)](https://circleci.com/gh/EvgeneKiiski/pbson/tree/master)
[![Coverage Status](https://coveralls.io/repos/github/EvgeneKiiski/pbson/badge.svg?branch=master)](https://coveralls.io/github/EvgeneKiiski/pbson?branch=master)

# PBson - Pure BSON

pbson is a BSON library for Scala.

The goal of this library is to create at compile-time the boilerplate necessary to encode and decode of a certain type.
The pbson provides generic codec derivation using [Shapeless](https://github.com/milessabin/shapeless).
This library provides another way encode and decode case classes for 
[mongo scala driver](https://docs.mongodb.com/ecosystem/drivers/scala/#mongo-scala-driver).
Decoder instead of throw Exception return ```Either[BsonError, T]```
    

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

The current stable version is 0.0.15

If you're using SBT, add the following line to your build file:

```scala
resolvers += "JCenter" at "https://jcenter.bintray.com/"
libraryDependencies += "ru.twistedlogic" %% "pbson" % "0.0.15"
```

## Resources

[docs](https://evgenekiiski.github.io/pbson/)

[examples](https://github.com/EvgeneKiiski/pbson/blob/master/examples/src/main/scala/pbson/examples/)

## Cats 

[pbson-cats](https://github.com/EvgeneKiiski/pbson-cats)

## Scalaz

[pbson-scalaz under construction](https://evgenekiiski.github.io/pbson/)


