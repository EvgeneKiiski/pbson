package pbson

import org.mongodb.scala.bson.{BsonDocument, BsonInt32, BsonString}
import org.scalatest.{Matchers, ParallelTestExecution, WordSpec}

/**
  * @author Evgenii Kiiski 
  */
class EitherTest extends WordSpec with ParallelTestExecution with Matchers {

  implicit val eitherTestEncoder: BsonEncoder[Either[String, Int]] = eitherEncoder("left", "right")
  implicit val eitherTestDecoder: BsonDecoder[Either[String, Int]] = eitherDecoder("left", "right")

  "Either" should {
    "encode right" in {
      val test: Either[String, Int] = Right(1)
      val bson = test.toBson
      bson.asDocument() shouldEqual BsonDocument("right" -> BsonInt32(1))
    }
    "encode left" in {
      val test: Either[String, Int] = Left("test")
      val bson = test.toBson
      bson.asDocument() shouldEqual BsonDocument("left" -> BsonString("test"))
    }
    "decode right" in {
      val bson = BsonDocument("right" -> BsonInt32(1))
      bson.fromBson[Either[String, Int]]() shouldEqual Right(Right(1))
    }
    "decode left" in {
      val bson = BsonDocument("left" -> BsonString("test"))
      bson.fromBson[Either[String, Int]]() shouldEqual Right(Left("test"))
    }
    "decode field non found" in {
      val bson = BsonDocument("thrid" -> BsonString("test"))
      bson.fromBson[Either[String, Int]]().isLeft shouldEqual true
    }
    "decode wrong type" in {
      val bson = BsonString("test")
      bson.fromBson[Either[String, Int]]().isLeft shouldEqual true
    }
  }

}
