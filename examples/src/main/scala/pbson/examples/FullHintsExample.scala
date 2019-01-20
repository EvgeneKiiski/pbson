package pbson.examples

import org.mongodb.scala.bson.{BsonDocument, BsonString, BsonValue}
import pbson.BsonDecoder.Result
import pbson.BsonError.InvalidType
import pbson._
import pbson.semiauto._

/**
  * @author Evgenii Kiiski 
  */
object FullHintsExample extends App {

  case class OwnTypeA(value: String) extends AnyVal

  case class OwnType(value: String) extends AnyVal


  sealed trait SealedTest

  object SealedTest {

    final case class One() extends SealedTest

    final case class Two(s: String) extends SealedTest

  }

  import SealedTest._

  implicit val oneEncoder: BsonEncoder[One] = deriveEncoder
  implicit val twoEncoder: BsonEncoder[Two] = deriveEncoder
  implicit val sealedEncoder: BsonEncoder[SealedTest] = deriveEncoder

  implicit val oneDecoder: BsonDecoder[One] = deriveDecoder
  implicit val twoDecoder: BsonDecoder[Two] = deriveDecoder
  implicit val sealedDecoder: BsonDecoder[SealedTest] = deriveDecoder

  sealed trait ADTEnum

  object ADTEnum {

    case object A extends ADTEnum

    case class B() extends ADTEnum

    case class C() extends ADTEnum

  }

  import ADTEnum._

  implicit val adtEnumEncoder: BsonEncoder[ADTEnum] = asStringEncoder {
    case A => "A"
    case B() => "B"
    case C() => "C"
  }

  implicit val adtEnumDecoder: BsonDecoder[ADTEnum] = asStringDecoder {
    case "A" => A
    case "B" => B()
    case "C" => C()
  }

  case class NestedCase(a: String, b: Long)

  case class TestCase(
                       w: OwnTypeA,
                       e: Map[OwnType, NestedCase],
                       st: SealedTest,
                       en: ADTEnum
                     )

  implicit val nestedCaseEncoder: BsonEncoder[NestedCase] = deriveEncoder
  implicit val nestedCaseDecoder: BsonDecoder[NestedCase] = deriveDecoder

  implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
  implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder


  val test = TestCase(
    OwnTypeA("sssss"),
    Map(OwnType("32") -> NestedCase("r", 5), OwnType("12") -> NestedCase("d", 1)),
    Two("99"),
    A
  )

  val bson = test.toBson
  println(bson)
  println(bson.fromBson[TestCase])

}
