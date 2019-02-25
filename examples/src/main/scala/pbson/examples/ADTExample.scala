package pbson.examples

import pbson._
import pbson.semiauto._

/**
  * @author Evgenii Kiiski
  * @see [[https://evgenekiiski.github.io/pbson/]]
  */
object ADTExample extends App {

  sealed trait ADT

  object ADT {

    case class A(s: String) extends ADT

    case class B(a: Int) extends ADT

  }

  import ADT._

  implicit val adtEncoder: BsonEncoder[ADT] = deriveEncoder
  implicit val adtDecoder: BsonDecoder[ADT] = deriveDecoder

  val test : ADT = B(4)

  val bson = test.toBson
  println(bson)
  println(bson.fromBson[ADT])


}
