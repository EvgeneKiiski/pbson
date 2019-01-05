package pbson.tchar

import org.mongodb.scala.bson.BsonString
import org.scalatest.{Matchers, ParallelTestExecution, WordSpec}
import pbson.BsonDecoder

/**
  * @author Evgenii Kiiski 
  */
class CharTest extends WordSpec with ParallelTestExecution with Matchers {

  "CharDecoder" should {
    "invalid length" in {
      BsonDecoder[Char].apply(BsonString("")).isLeft shouldEqual true
      BsonDecoder[Char].apply(BsonString("aa")).isLeft shouldEqual true
    }
  }


}
