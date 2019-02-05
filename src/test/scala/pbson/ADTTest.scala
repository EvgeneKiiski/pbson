package pbson

import org.mongodb.scala.bson.{BsonDocument, BsonInt32, BsonString, BsonValue}
import org.scalatest.{Matchers, ParallelTestExecution, WordSpec}
import pbson.BsonError.ADTValueNotFound
import pbson.semiauto.{deriveDecoder, deriveEncoder}

/**
  * @author Evgenii Kiiski 
  */
class ADTTest extends WordSpec with ParallelTestExecution with Matchers  {

  sealed trait ADT

  object ADT {

    case class A(s: String) extends ADT
    case class B(a: Int) extends ADT
    case class C() extends ADT

  }

  import ADT._

  implicit val adtEncoder: BsonEncoder[ADT] = deriveEncoder
  implicit val adtDecoder: BsonDecoder[ADT] = deriveDecoder

  "AnyVal wrapper" should {
    "encode" in {
      val test : ADT = B(4)
      test.toBson shouldEqual BsonDocument("type" -> BsonString("B"), "a" -> BsonInt32(4))
    }
    "decode" in {
      val bson: BsonValue = BsonDocument("type" -> BsonString("B"), "a" -> BsonInt32(4))
      bson.fromBson[ADT] shouldEqual Right(B(4))
    }
    "decode unexpected type" in {
      val bson: BsonValue = BsonDocument("type" -> BsonString("E"), "a" -> BsonInt32(4))
      bson.fromBson[ADT] shouldEqual Left(ADTValueNotFound)
    }
  }

}
