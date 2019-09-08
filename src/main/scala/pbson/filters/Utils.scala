package pbson.filters

import org.bson.{ BsonDocument, BsonValue }
import pbson.BsonEncoder

/**
  * @author Evgenii Kiiski 
  */
object Utils {

  @inline final def includeOperator[A](key: String, operator: String, value: A)(implicit
    e: BsonEncoder[A]
  ): BsonDocument = new BsonDocument(key, new BsonDocument(operator, e(value)))

  @inline final def includeOperatorBson[A](key: String, operator: String, value: BsonValue): BsonDocument =
    new BsonDocument(key, new BsonDocument(operator, value))

}
