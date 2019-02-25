package pbson.examples

import pbson._
import pbson.semiauto._

/**
  * @author Eugene Kiyski
  * @see [[https://evgenekiiski.github.io/pbson/]]
  */
object MapExample extends App {

  case class TestCase(a: Map[String, String])

  implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
  implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

  val test : TestCase = TestCase(Map("key1" -> "value1", "key2" -> "value2"))

  val bson = test.toBson
  println(bson)
  println(bson.fromBson[TestCase])

}
