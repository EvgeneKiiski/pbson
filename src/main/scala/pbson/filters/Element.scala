package pbson.filters

import org.bson.{ BsonBoolean, BsonDocument, BsonString }
import pbson.filters.Utils.includeOperatorBson

/**
  * @author Evgenii Kiiski 
  */
trait Element {

  /**
    * Matches documents that have the specified field.
    */
  final def exist(key: String, exist: Boolean = true): BsonDocument =
    includeOperatorBson(key, "$exists", new BsonBoolean(exist))

  /**
   *  Selects documents if a field is of the specified type.
   */

  final def bsonType(key: String, tName: String): BsonDocument =
    includeOperatorBson(key, "$type", new BsonString(tName))

}
