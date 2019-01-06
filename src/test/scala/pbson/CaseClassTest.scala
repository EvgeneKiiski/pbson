package pbson

import org.scalatest.{Matchers, ParallelTestExecution, WordSpec}
import pbson.semiauto._

/**
  * @author Evgenii Kiiski 
  */
class CaseClassTest extends WordSpec with ParallelTestExecution with Matchers {

    "Decode Encode" should {
      "simple example" in {
        case class TestCase(a: Int, b: Option[String])

        implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
        implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder

        val test = TestCase(3, Some("45"))

        val bson = test.toBson
        bson.fromBson[TestCase] shouldEqual Right(test)
      }
    }

}
