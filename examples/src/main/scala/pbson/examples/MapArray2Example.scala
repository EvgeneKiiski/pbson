package pbson.examples

import pbson._
import pbson.encoder.ReprBsonMaybeEncoder
import pbson.semiauto._

/**
  * @author Eugene Kiyski
  * [[https://evgenekiiski.github.io/pbson/]]
  */
object MapArray2Example extends App {

  case class Key(k: String)
  case class Value(v: String)
  case class TestCase(a: Map[Key, Value])


  implicit val mapEncoder: ReprBsonMaybeEncoder[Map[Key, Value]] = map2ArrayEncoder
  implicit val mapDecoder: BsonDecoder[Map[Key, Value]] = array2MapDecoder

  implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
  implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

  val test : TestCase = TestCase(Map(Key("45") -> Value("34"), Key("23") -> Value("45")))

  val bson = test.toBson
  println(bson)
  println(bson.fromBson[TestCase]())

}
