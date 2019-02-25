package pbson.examples

import pbson._
import pbson.semiauto._

/**
  * @author Eugene Kiyski
  * @see [[https://evgenekiiski.github.io/pbson/]]
  */
object MapArrayExample extends App {

  case class TestCase(a: Map[String, String])

  implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
  implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

  implicit val mapEncoder: BsonEncoder[Map[String, String]] = map2ArrayEncoder
  implicit val mapDecoder: BsonDecoder[Map[String, String]] = array2MapDecoder

  val test : TestCase = TestCase(Map("key1" -> "value1", "key2" -> "value2"))

  val bson = test.toBson
  println(bson)
  println(bson.fromBson[TestCase])

}
