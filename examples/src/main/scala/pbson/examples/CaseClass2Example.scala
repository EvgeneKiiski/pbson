package pbson.examples

import pbson._
import pbson.semiauto._

/**
  * @author Evgenii Kiiski
  * [[https://evgenekiiski.github.io/pbson/]]
  */
object CaseClass2Example extends App {

  sealed trait SealedTest

  object SealedTest {
    final case class One() extends SealedTest
    final case class Two(s: String) extends SealedTest

  }

  import SealedTest._

  implicit val sealedEncoder: BsonEncoder[SealedTest] = deriveEncoder

  implicit val sealedDecoder: BsonDecoder[SealedTest] = deriveDecoder

  case class NestedCase(a: String, b: Long)

  case class TestCase(
                       a: Int,
                       b: Option[String],
                       c: Long,
                       d: Seq[Long],
                       e: Map[String, NestedCase],
                       st: SealedTest
                     )


  implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
  implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder


  val test = TestCase(
    3,
    Some("45"),
    34l,
    List(2l, 5l),
    Map("32" -> NestedCase("r", 5)),
    One()
  )

  val bson = test.toBson
  println(bson)
  println(bson.fromBson[TestCase])

}
