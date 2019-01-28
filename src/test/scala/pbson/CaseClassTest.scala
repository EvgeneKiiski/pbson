package pbson

import org.bson.BsonType
import org.mongodb.scala.bson.{BsonDocument, BsonString}
import org.scalatest.{Matchers, ParallelTestExecution, WordSpec}
import pbson.BsonError.UnexpectedType
import pbson.semiauto._

/**
  * @author Evgenii Kiiski 
  */

class CaseClassTest extends WordSpec with ParallelTestExecution with Matchers {

  import CaseClassTest._


    "Decode Encode" should {
      "simple example" in {
        case class TestCase(a: Int, b: Option[String], id: MyId)

        implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
        implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

        val test = TestCase(3, Some("45"), MyId("000"))

        val bson = test.toBson
        bson.fromBson[TestCase] shouldEqual Right(test)
      }
      "invalid type" in {
        case class TestCase(a: Int, b: Option[String])

        implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
        implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

        val test = TestCase(3, Some("45"))

        val bson = BsonString("343")
        bson.fromBson[TestCase] shouldEqual Left(UnexpectedType(BsonString("343"), BsonType.DOCUMENT))
      }
      "simple example wrapped with either constructor" in {

        case class TestCase(a: Int, b: Option[String], id: MyIdV)

        implicit val idDecoder: BsonDecoder[MyIdV] = validateDeriveDecoder(MyIdV.validate)

        implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
        implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

        val test = TestCase(3, Some("45"), MyIdV("000"))

        val bson = test.toBson
        bson.fromBson[TestCase] shouldEqual Right(test)
      }
      "seq map" in {

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
                             a: Int,
                             b: Option[String],
                             c: Long,
                             d: Seq[Long],
                             e: Map[String, NestedCase],
                             st: SealedTest
                           )


        implicit val nestedCaseEncoder: BsonEncoder[NestedCase] = deriveEncoder
        implicit val nestedCaseDecoder: BsonDecoder[NestedCase] = deriveDecoder
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
        bson.fromBson[TestCase] shouldEqual Right(test)

      }
    }

}
object CaseClassTest {

  final case class MyId(value: String) extends AnyVal

  final case class MyIdV(value: String) extends AnyVal

  object MyIdV {
    private[CaseClassTest] def apply(value: String): MyIdV = new MyIdV(value)

    def validate(value: MyIdV): Either[BsonError, MyIdV] = Right(value)
  }

}
