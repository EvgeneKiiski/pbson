package pbson.examples

import pbson._
import pbson.semiauto._

/**
  * @author Evgenii Kiiski 
  */
object ADTExample extends App {

  sealed trait ADT

  object ADT {

    case class A(s: String) extends ADT

    case class B(a: Int) extends ADT

    implicit val aEncoder: BsonEncoder[A] = deriveEncoder
    implicit val aDecoder: BsonDecoder[A] = deriveDecoder

    implicit val bEncoder: BsonEncoder[B] = deriveEncoder
    implicit val bDecoder: BsonDecoder[B] = deriveDecoder

  }

  import ADT._

  implicit val adtEncoder: BsonEncoder[ADT] = deriveEncoder
  implicit val adtDecoder: BsonDecoder[ADT] = deriveDecoder

  val test : ADT = B(4)

  val bson = test.toBson
  println(bson)
  println(bson.fromBson[ADT])


}
