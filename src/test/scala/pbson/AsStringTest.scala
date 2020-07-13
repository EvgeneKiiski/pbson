package pbson

import org.bson.BsonType
import org.mongodb.scala.bson.{BsonInt32, BsonString}
import org.scalatest.{EitherValues, Matchers, ParallelTestExecution, WordSpec}
import pbson.BsonError._

/**
  * @author Eugene Kiyski
  */
class AsStringTest extends WordSpec with ParallelTestExecution with Matchers with EitherValues {

  sealed trait ADT

  object ADT {
    final case object One extends ADT
    final case object Two extends ADT
  }

  implicit val adtEnumEncoder: BsonEncoder[ADT] = asStringEncoder {
    case ADT.One => "A"
    case ADT.Two => "B"
  }

  implicit val adtEnumDecoder: BsonDecoder[ADT] = asStringDecoder {
    case "A" => ADT.One
    case "B" => ADT.Two
  }

  "ADT root level" should {
    "encode" in {
      val test: ADT = ADT.One
      val bson = test.toBson
      bson shouldEqual BsonString("A")
    }
    "decode" in {
      val bson = BsonString("A")
      bson.fromBson[ADT]() shouldEqual Right(ADT.One)
    }
    "decode invalid" in {
      val bson = BsonString("C")
      bson.fromBson[ADT]() shouldEqual Left(UnexpectedValue("C"))
    }
    "decode invalid type" in {
      val bson = BsonInt32(12)
      bson.fromBson[ADT]() shouldEqual Left(UnexpectedType(BsonInt32(12), BsonType.STRING))
    }
  }


}
