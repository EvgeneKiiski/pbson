package pbson.filters

import org.bson.BsonDocument
import pbson.BsonEncoder
import Utils._
import collection.JavaConverters._

/**
  * @author Evgenii Kiiski 
  */
trait Comparison {
  /**
    * eq	Matches values that are equal to a specified value.
    */
  final def equal[A](key: String, value: A)(implicit e: BsonEncoder[A]): BsonDocument =
    new BsonDocument(key, e.apply(value))

  /**
    * gt	Matches values that are greater than a specified value.
    */
  final def gt[A](key: String, value: A)(implicit e: BsonEncoder[A]): BsonDocument =
    includeOperator(key, "$gt", value)

  /**
    * gte	Matches values that are greater than or equal to a specified value.
    */
  final def gte[A](key: String, value: A)(implicit e: BsonEncoder[A]): BsonDocument =
    includeOperator(key, "$gte", value)

  /**
    * lt	Matches values that are less than a specified value.
    */
  final def lt[A](key: String, value: A)(implicit e: BsonEncoder[A]): BsonDocument =
    includeOperator(key, "$lt", value)

  /**
    * lte	Matches values that are less than or equal to a specified value.
    */
  final def lte[A](key: String, value: A)(implicit e: BsonEncoder[A]): BsonDocument =
    includeOperator(key, "$lte", value)

  /**
    * ne	Matches all values that are not equal to a specified value.
    */
  final def ne[A](key: String, value: A)(implicit e: BsonEncoder[A]): BsonDocument =
    includeOperator(key, "$ne", value)

  /**
    * in	Matches any of the values specified in an array.
    */
  final def in[A](key: String, values: Seq[A])(implicit e: BsonEncoder[A]): BsonDocument =
    includeOperatorBson(key, "$in", new BsonArray(values.map(e.apply).asJava))

  /**
    * nin	Matches none of the values specified in an array.
    */
  final def nin[A](key: String, values: Seq[A])(implicit e: BsonEncoder[A]): BsonDocument =
    includeOperatorBson(key, "$nin", new BsonArray(values.map(e.apply).asJava))
}
