package pbson

import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonString}
import org.scalatest.{Matchers, ParallelTestExecution, WordSpec}
import pbson._
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
      bson.asDocument().get("a") shouldEqual BsonDocument(
        "45" -> BsonString("34"),
        "23" -> BsonString("45")
      )
    }
    "encode map empty" in {
      val test = TestCase(Map.empty)
      val bson = test.toBson
      println(bson)
      bson.asDocument().containsKey("a") shouldEqual false
    }
    "decode map" in {
      val bson = BsonDocument(
        "a" -> BsonDocument(
          "45" -> BsonString("34"),
          "23" -> BsonString("45")
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
      bson.asDocument().get("a") shouldEqual BsonDocument(
        "45" -> BsonDocument("sd" -> BsonString("34")),
        "23" -> BsonDocument("sd" -> BsonString("45"))
      )
    }
    "encode map empty" in {
      val test = TestCase(Map.empty)
      val bson = test.toBson
      println(bson)
      bson.asDocument().containsKey("a") shouldEqual false
    }
    "decode map" in {
      val bson = BsonDocument(
        "a" -> BsonDocument(
          "45" -> BsonDocument("sd" -> BsonString("34")),
          "23" -> BsonDocument("sd" -> BsonString("45"))
        ))
      bson.fromBson[TestCase] shouldEqual Right(TestCase(Map("45" -> Nested("34"), "23" -> Nested("45"))))
    }
    "decode map empty" in {
      val bson = BsonDocument()
      bson.fromBson[TestCase] shouldEqual Right(TestCase(Map.empty))
    }
  }

  "Map case -> case" should {

    case class Key(k: String)
    case class Nested(sd: String)
    case class TestCase(a: Map[Key, Nested])

    implicit val keyEncoder: BsonEncoder[Key] = deriveEncoder
    implicit val keyDecoder: BsonDecoder[Key] = deriveDecoder

    implicit val nestedEncoder: BsonEncoder[Nested] = deriveEncoder
    implicit val nestedDecoder: BsonDecoder[Nested] = deriveDecoder

    implicit val mapEncoder: BsonEncoder[Map[Key, Nested]] = map2ArrayEncoder
    implicit val mapDecoder: BsonDecoder[Map[Key, Nested]] = array2MapDecoder

    implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
    implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

    "encode map" in {
      val test = TestCase(Map(Key("45") -> Nested("34"), Key("23") -> Nested("45")))
      val bson = test.toBson
      bson.asDocument().get("a") shouldEqual BsonArray(
        BsonDocument(
          "k" -> BsonString("45"),
          "sd" -> BsonString("34")
        ),
        BsonDocument(
          "k" -> BsonString("23"),
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
      val bson = BsonDocument(
        "a" -> BsonArray(
          BsonDocument(
            "k" -> BsonString("45"),
            "sd" -> BsonString("34")
          ),
          BsonDocument(
            "k" -> BsonString("23"),
            "sd" -> BsonString("45")
          )
        )
      )
      bson.fromBson[TestCase] shouldEqual Right(TestCase(Map(Key("45") -> Nested("34"), Key("23") -> Nested("45"))))
    }
    "decode map empty" in {
      val bson = BsonDocument()
      bson.fromBson[TestCase] shouldEqual Right(TestCase(Map.empty))
    }
  }

}
