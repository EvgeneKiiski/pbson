package pbson

import org.bson.BsonType
import org.mongodb.scala.bson.{BsonInt32, BsonString, BsonValue}
import org.scalatest.{Matchers, ParallelTestExecution, WordSpec}
import pbson.BsonError.UnexpectedType
import pbson.semiauto.{deriveDecoder, deriveEncoder}

/**
  * @author Eugene Kiyski
  */
class AnyValWrapperTest extends WordSpec with ParallelTestExecution with Matchers {

  import AnyValWrapperTest._

  "AnyVal wrapper" should {
    val test = Wrapper("000")
    "encode" in {
      val bson = test.toBson
      println(bson)
      bson shouldEqual BsonString("000")
    }
    "decode" in {
      val bson : BsonValue = BsonString("000")
      bson.fromBson[Wrapper]() shouldEqual Right(test)
    }
    "decode invalid value" in {
      val bson : BsonValue = BsonInt32(23)
      bson.fromBson[Wrapper]() shouldEqual Left(UnexpectedType(bson, BsonType.STRING))
    }
  }

}

object AnyValWrapperTest {
  case class Wrapper(value: String) extends AnyVal

  implicit val wrapperEncoder: BsonEncoder[Wrapper] = deriveEncoder
  implicit val wrapperDecoder: BsonDecoder[Wrapper] = deriveDecoder
}
