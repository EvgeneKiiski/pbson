package pbson

import org.scalatest.{Matchers, WordSpec}
import pbson.semiauto._

/**
  * @author Evgenii Kiiski 
  */
class CaseClass2Test extends WordSpec with Matchers {


  import CaseClass2Test.SealedTest
  import CaseClass2Test.SealedTest._


  implicit val sealedEncoder: BsonEncoder[SealedTest] = deriveEncoder
  implicit val sealedDecoder: BsonDecoder[SealedTest] = deriveDecoder

  case class NestedCase(a: String, b: Long)

  case class TestCase(
                       a: Int,
                       b: Option[String],
                       c: Long,
                       d: Seq[Long],
                       //e: Map[String, NestedCase],
                       st: SealedTest
                     )

  implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
  implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

  "Decode Encode" should {
    "seq map" in {


      val test = TestCase(
        3,
        Some("45"),
        34l,
        List(2l, 5l),
        //Map("32" -> NestedCase("r", 5)),
        One()
      )

      val bson = test.toBson
      bson.fromBson[TestCase] shouldEqual Right(test)

    }
  }

}

object CaseClass2Test {

  sealed trait SealedTest

  object SealedTest {

    final case class One() extends SealedTest

    final case class Two(s: String) extends SealedTest

  }

}
