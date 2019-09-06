package pbson.filters

import org.bson.BsonDocument
import org.bson.conversions.Bson
import pbson.BsonEncoder

/**
  * @author Evgenii Kiiski 
  */
trait Comparison {
  /**
    * $eq	Matches values that are equal to a specified value.
    */
  final def eq[A](key: String, value: A)(implicit e: BsonEncoder[A]): Bson =
    new BsonDocument(key, e.apply(value))

   @inline private def incDoc[A](key: String, operator:String, value: A)(implicit
    e: BsonEncoder[A]
  ): Bson = new BsonDocument(key, new BsonDocument(operator, e(value)))

  /**
    * $gt	Matches values that are greater than a specified value.
    */
  final def gt[A](key: String, value: A)(implicit e: BsonEncoder[A]): Bson =
    incDoc(key, "$gt", value)

  /**
    * $gte	Matches values that are greater than or equal to a specified value.
    */
  final def gte[A](key: String, value: A)(implicit e: BsonEncoder[A]): Bson =
    incDoc(key, "$gte", value)

  /**
    * $lt	Matches values that are less than a specified value.
    */
  final def lt[A](key: String, value: A)(implicit e: BsonEncoder[A]): Bson =
    incDoc(key, "$lt", value)

  /**
    * $lte	Matches values that are less than or equal to a specified value.
    */
  final def lte[A](key: String, value: A)(implicit e: BsonEncoder[A]): Bson =
    incDoc(key, "$lte", value)

  /**
    * $ne	Matches all values that are not equal to a specified value.
    */
  final def ne[A](key: String, value: A)(implicit e: BsonEncoder[A]): Bson =
    incDoc(key, "$ne", value)

  /**
    * $in	Matches any of the values specified in an array.
    */
  /**
    * $nin	Matches none of the values specified in an array.
    */
}
