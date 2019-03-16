package pbson

import org.bson.BsonType
import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonInt32, BsonString}
import org.scalatest.{Matchers, ParallelTestExecution, WordSpec}
import pbson.BsonError.UnexpectedType
import pbson.semiauto._

/**
  * @author Eugene Kiyski
  */
class VectorTest extends WordSpec with ParallelTestExecution with Matchers {

  case class TestCase(a: Vector[String])

  implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
  implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

  "Vector" should {
    "encode vector" in {
      val test = TestCase(Vector("45", "23"))
      val bson = test.toBson
      bson.asDocument().get("a") shouldEqual BsonArray(BsonString("45"), BsonString("23"))
    }
    "encode vector empty" in {
      val test = TestCase(Vector.empty)
      val bson = test.toBson
      bson.asDocument().containsKey("a") shouldEqual false
    }
    "decode vector" in {
      val bson = BsonDocument("a" -> BsonArray(BsonString("45"), BsonString("23")))
      bson.fromBson[TestCase] shouldEqual Right(TestCase(Vector("45", "23")))
    }
    "decode vector empty" in {
      val bson = BsonDocument()
      bson.fromBson[TestCase] shouldEqual Right(TestCase(Vector.empty))
    }
    "decode unexpected type" in {
      val bson = BsonDocument("a" -> BsonString("45"))
      bson.fromBson[TestCase] shouldEqual Left(UnexpectedType(BsonString("45"), BsonType.ARRAY))
    }
    "decode vector with error" in {
      val bson = BsonDocument("a" -> BsonArray(BsonString("45"), BsonInt32(23)))
      bson.fromBson[TestCase] shouldEqual Left(UnexpectedType(BsonInt32(23), BsonType.STRING))
    }
  }

}
