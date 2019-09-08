package pbson.filters

import org.mongodb.scala.bson.{ BsonDocument, BsonInt32, BsonString }
import org.scalatest.{ Assertions, ParallelTestExecution, WordSpec }

/**
  * @author Evgenii Kiiski 
  */
class FiltersTest extends WordSpec with ParallelTestExecution with Assertions {

  "Filters" should {
    "example" in {
      val filter = and(equal("name", "Valia"), lt("age", 50))

      println(filter)
      assert{
        filter == new BsonDocument("$and", BsonArray(
          new BsonDocument("name", BsonString("Valia")),
          new BsonDocument("age", new BsonDocument("$lt", BsonInt32(50)))
        ))
      }
    }
  }

}
