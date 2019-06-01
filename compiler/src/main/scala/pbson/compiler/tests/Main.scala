package pbson.compiler.tests

import pbson._
import pbson.semiauto._

/**
  * @author Evgenii Kiiski 
  */
object Main extends App {

  case class PropertyString(s: String) extends AnyVal

  implicit final val sEncoder: BsonEncoder[PropertyString] = deriveEncoder
  implicit final val sDecoder: BsonDecoder[PropertyString] = deriveDecoder

  case class PropertyInt(v: Int) extends AnyVal

  implicit final val iEncoder: BsonEncoder[PropertyInt] = deriveEncoder
  implicit final val iDecoder: BsonDecoder[PropertyInt] = deriveDecoder

  case class PropertyLong(d: Long) extends AnyVal

  implicit final val lEncoder: BsonEncoder[PropertyLong] = deriveEncoder
  implicit final val lDecoder: BsonDecoder[PropertyLong] = deriveDecoder

  case class Nested(
                   id: PropertyLong,
                   name: PropertyString
                   )

  case class TestCase(
                     id: PropertyLong,
                     cap: PropertyInt,
                     seq: List[Nested],
                     description: PropertyString
                     )

  implicit final val tEncoder: BsonEncoder[TestCase] = deriveEncoder
  implicit final val tDecoder: BsonDecoder[TestCase] = deriveDecoder


  val test = TestCase(
    PropertyLong(1l),
    PropertyInt(3),
    List(
      Nested(PropertyLong(1l), PropertyString("Ivan")),
      Nested(PropertyLong(2l), PropertyString("Petr")),
      Nested(PropertyLong(3l), PropertyString("Mary"))
    ),
    PropertyString("Test value")
  )

  val bson = test.toBson

  //val bsonOne = test.toBson

  val result = bson.fromBson[TestCase]()

  //val resultOne = bsonOne.fromBson[TestCase]()

  println(result)

  //println(resultOne)

}
