package pbson.examples

import pbson._

/**
  * @author Evgenii Kiiski
  * [[https://evgenekiiski.github.io/pbson/]]
  */
object EnumExample extends App {

  sealed trait ADT

  object ADT {
    final case object A extends ADT
    final case object B extends ADT
  }

  import ADT._

  implicit val adtEnumEncoder: BsonEncoder[ADT] = asStringEncoder {
    case A => "A"
    case B => "B"
  }

  implicit val adtEnumDecoder: BsonDecoder[ADT] = asStringDecoder {
    case "A" => A
    case "B" => B
  }

  val test : ADT = B

  val bson = test.toBson
  println(bson)
  println(bson.fromBson[ADT])


}
