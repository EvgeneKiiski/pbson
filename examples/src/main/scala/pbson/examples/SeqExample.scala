package pbson.examples

import pbson._
import pbson.semiauto._

/**
  * @author Eugene Kiyski
  * @see [[https://evgenekiiski.github.io/pbson/]]
  */
object SeqExample extends App {

  case class TestCase(a: Seq[String])

  implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
  implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

  val test : TestCase = TestCase(Seq("str1", "str2"))

  val bson = test.toBson
  println(bson)
  println(bson.fromBson[TestCase])

}
