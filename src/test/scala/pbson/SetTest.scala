package pbson

import org.bson.BsonType
import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonString}
import org.scalatest.{Matchers, ParallelTestExecution, WordSpec}
import pbson.BsonError.UnexpectedType
import pbson.semiauto._

/**
  * @author Eugene Kiyski
  */
class SetTest extends WordSpec with ParallelTestExecution with Matchers {

  case class TestCase(a: Set[String])

  implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
  implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

  "Set" should {
    "encode list" in {
      val test = TestCase(Set("45", "23"))
      val bson = test.toBson
      bson.asDocument().get("a") shouldEqual BsonArray(BsonString("45"), BsonString("23"))
    }
    "encode list empty" in {
      val test = TestCase(Set.empty)
      val bson = test.toBson
      bson.asDocument().containsKey("a") shouldEqual false
    }
    "decode list" in {
      val bson = BsonDocument("a" -> BsonArray(BsonString("45"), BsonString("23")))
      bson.fromBson[TestCase] shouldEqual Right(TestCase(Set("45", "23")))
    }
    "decode list empty" in {
      val bson = BsonDocument()
      bson.fromBson[TestCase] shouldEqual Right(TestCase(Set.empty))
    }
    "decode unexpected type" in {
      val bson = BsonDocument("a" -> BsonString("45"))
      bson.fromBson[TestCase] shouldEqual Left(UnexpectedType(BsonString("45"), BsonType.ARRAY))
    }
  }

}
