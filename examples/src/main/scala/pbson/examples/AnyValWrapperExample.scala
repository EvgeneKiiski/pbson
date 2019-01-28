package pbson.examples

import pbson._
import pbson.semiauto._

/**
  * @author Evgenii Kiiski 
  */
object AnyValWrapperExample extends App {

  case class MyId(value: String) extends AnyVal

  implicit val testCaseEncoder: BsonEncoder[MyId] = deriveEncoder
  implicit val testCaseDecoder: BsonDecoder[MyId] = deriveDecoder

  val test = MyId("000")

  val bson = test.toBson
  println(bson)
  println(bson.fromBson[MyId])


}
