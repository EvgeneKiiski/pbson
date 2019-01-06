package pbson.examples

import pbson._
import pbson.semiauto._

/**
  * @author Evgenii Kiiski 
  */
object SimpleExample extends App {

  case class TestCase(a: Int, b: Option[String])

  implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
  implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

  val test = TestCase(3, Some("45"))

  val bson = test.toBson
  println(bson)
  println(bson.fromBson[TestCase])


}
