package pbson

import org.mongodb.scala.bson.{BsonDocument, BsonString}
import org.scalatest.{Matchers, ParallelTestExecution, WordSpec}
import pbson.semiauto._

/**
  * @author Eugene Kiyski
  */
class OptionTest extends WordSpec with ParallelTestExecution with Matchers {

  case class TestCase(a: Option[String])

  implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
  implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

  "Options" should {
    "encode some" in {
      val test = TestCase(Some("45"))
      val bson = test.toBson
      bson.asDocument().get("a") shouldEqual BsonString("45")
    }
    "encode none" in {
      val test = TestCase(None)
      val bson = test.toBson
      bson.asDocument().containsKey("a") shouldEqual false
    }
    "decode some" in {
      val bson = BsonDocument("a" -> BsonString("12"))
      bson.fromBson[TestCase]() shouldEqual Right(TestCase(Some("12")))
    }
    "decode none" in {
      val bson = BsonDocument()
      bson.fromBson[TestCase]() shouldEqual Right(TestCase(None))
    }
  }

}
