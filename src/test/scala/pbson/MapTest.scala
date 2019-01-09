package pbson

import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonString}
import org.scalatest.{Matchers, ParallelTestExecution, WordSpec}
import pbson.semiauto._

/**
  * @author Eugene Kiyski
  */
class MapTest extends WordSpec with ParallelTestExecution with Matchers {

  "Map simple -> simple" should {
    case class TestCase(a: Map[String, String])

    implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
    implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

    "encode map" in {
      val test = TestCase(Map("45" -> "34", "23" -> "45"))
      val bson = test.toBson
      bson.asDocument().get("a") shouldEqual BsonArray(
        BsonDocument(
          "_k" -> BsonString("45"),
          "_v" -> BsonString("34")
        ),
        BsonDocument(
          "_k" -> BsonString("23"),
          "_v" -> BsonString("45")
        )
      )
    }
    "encode map empty" in {
      val test = TestCase(Map.empty)
      val bson = test.toBson
      println(bson)
      bson.asDocument().containsKey("a") shouldEqual false
    }
    "decode map" in {
      val bson = BsonDocument("a" -> BsonArray(
        BsonDocument(
          "_k" -> BsonString("45"),
          "_v" -> BsonString("34")
        ),
        BsonDocument(
          "_k" -> BsonString("23"),
          "_v" -> BsonString("45")
        )
      ))
      bson.fromBson[TestCase] shouldEqual Right(TestCase(Map("45" -> "34", "23" -> "45")))
    }
    "decode map empty" in {
      val bson = BsonDocument()
      bson.fromBson[TestCase] shouldEqual Right(TestCase(Map.empty))
    }
  }

  "Map simple -> case" should {

    case class Nested(sd: String)
    case class TestCase(a: Map[String, Nested])

    implicit val nestedEncoder: BsonEncoder[Nested] = deriveEncoder
    implicit val nestedDecoder: BsonDecoder[Nested] = deriveDecoder

    implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
    implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

    "encode map" in {
      val test = TestCase(Map("45" -> Nested("34"), "23" -> Nested("45")))
      val bson = test.toBson
      bson.asDocument().get("a") shouldEqual BsonArray(
        BsonDocument(
          "_k" -> BsonString("45"),
          "sd" -> BsonString("34")
        ),
        BsonDocument(
          "_k" -> BsonString("23"),
          "sd" -> BsonString("45")
        )
      )
    }
    "encode map empty" in {
      val test = TestCase(Map.empty)
      val bson = test.toBson
      println(bson)
      bson.asDocument().containsKey("a") shouldEqual false
    }
    "decode map" in {
      val bson = BsonDocument("a" -> BsonArray(
        BsonDocument(
          "_k" -> BsonString("45"),
          "sd" -> BsonString("34")
        ),
        BsonDocument(
          "_k" -> BsonString("23"),
          "sd" -> BsonString("45")
        )
      ))
      bson.fromBson[TestCase] shouldEqual Right(TestCase(Map("45" -> Nested("34"), "23" -> Nested("45"))))
    }
    "decode map empty" in {
      val bson = BsonDocument()
      bson.fromBson[TestCase] shouldEqual Right(TestCase(Map.empty))
    }
  }

}
