package pbson.examples

import org.mongodb.scala.bson.BsonString
import pbson._
import pbson.semiauto._

/**
  * @author Evgenii Kiiski 
  */
object FullHintsExample extends App {

  case class OwnTypeA(value: String) extends AnyVal

  implicit val ownTypeAEncoder: BsonEncoder[OwnTypeA] = deriveEncoder
  implicit val ownTypeADecoder: BsonDecoder[OwnTypeA] = deriveDecoder

  case class OwnType(value: String) extends AnyVal

  implicit val ownTypeEncoder: BsonEncoder[OwnType] = deriveEncoder
  implicit val ownTypeDecoder: BsonDecoder[OwnType] = deriveDecoder

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

  case class NestedCase(a: String, b: Long)

  case class TestCase(
                       w: OwnTypeA,
                       e: Map[OwnType, NestedCase],
                       st: SealedTest
                     )

  implicit val nestedCaseEncoder: BsonEncoder[NestedCase] = deriveEncoder
  implicit val nestedCaseDecoder: BsonDecoder[NestedCase] = deriveDecoder

  implicit val mapHintEncoder: BsonMapEncoder[OwnType, NestedCase] = mapKeyHintEncoder("key")
  implicit val mapHintDecoder: BsonMapDecoder[OwnType, NestedCase] = mapKeyHintDecoder("key")

  implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
  implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder


  val test = TestCase(
    OwnTypeA("sssss"),
    Map(OwnType("32") -> NestedCase("r", 5), OwnType("12") -> NestedCase("d", 1)),
    One()
  )

  val bson = test.toBson
  println(bson)
  println(bson.fromBson[TestCase])

}
