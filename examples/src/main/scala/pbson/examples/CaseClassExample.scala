package pbson.examples

import pbson._
import pbson.semiauto._

/**
  * @author Evgenii Kiiski
  * [[https://evgenekiiski.github.io/pbson/]]
  */
object CaseClassExample extends App {

  case class MyId(value: String) extends AnyVal

  case class TestCase(a: Int, b: Option[String], id: MyId)

  implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
  implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

  val test = TestCase(3, Some("45"), MyId("000"))

  val bson = test.toBson
  println(bson)
  println(bson.fromBson[TestCase]())


}
