package pbson

import org.mongodb.scala.bson.BsonString
import org.scalatest.{ Matchers, ParallelTestExecution, WordSpec }
import pbson.semiauto.{ asStringDecoder, asStringEncoder }

/**
  * @author Eugene Kiyski
  */
class SealedTraitTest extends WordSpec with ParallelTestExecution with Matchers {

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
  }


}
