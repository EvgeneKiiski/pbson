package pbson

import org.mongodb.scala.bson.{ BsonArray, BsonDocument, BsonString }
import org.scalatest.{ Matchers, ParallelTestExecution, WordSpec }
import pbson.semiauto._

/**
  * @author Eugene Kiyski
  */
class SeqTest extends WordSpec with ParallelTestExecution with Matchers {

  case class TestCase(a: Seq[String])

  implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
  implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

  "Seq" should {
    "encode seq" in {
      val test = TestCase(Seq("45", "23"))
      val bson = test.toBson
      bson.asDocument().get("a") shouldEqual BsonArray(BsonString("45"), BsonString("23"))
    }
    "encode seq empty" in {
      val test = TestCase(Seq.empty)
      val bson = test.toBson
      bson.asDocument().containsKey("a") shouldEqual false
    }
    "decode seq" in {
      val bson = BsonDocument("a" -> BsonArray(BsonString("45"), BsonString("23")))
      bson.fromBson[TestCase] shouldEqual Right(TestCase(Seq("45", "23")))
    }
    "decode seq empty" in {
      val bson = BsonDocument()
      bson.fromBson[TestCase] shouldEqual Right(TestCase(Seq.empty))
    }
  }

}
