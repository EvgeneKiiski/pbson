package pbson

import org.mongodb.scala.bson.BsonString
import org.scalatest.{ Matchers, ParallelTestExecution, WordSpec }
import pbson.CaseClassTest.MyId
import pbson.semiauto.{ deriveDecoder, deriveEncoder }

/**
  * @author Eugene Kiyski
  */
class CaseWrapperTest extends WordSpec with ParallelTestExecution with Matchers {

  import CaseWrapperTest._

  "Decode Encode" should {
    "simple example" in {

      implicit val wrapperEncoder: BsonEncoder[Wrapper] = deriveEncoder
      implicit val wrapperDecoder: BsonDecoder[Wrapper] = deriveDecoder

      val test = Wrapper("000")

      val bson = test.toBson
      println(bson)
      bson shouldEqual BsonString("000")
      bson.fromBson[Wrapper] shouldEqual Right(test)
    }
  }

}

object CaseWrapperTest {
  case class Wrapper(value: String) extends AnyVal
}
