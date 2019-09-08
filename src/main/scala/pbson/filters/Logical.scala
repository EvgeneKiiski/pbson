package pbson.filters

import org.bson.{ BsonDocument, BsonValue }
import pbson.filters.Utils.includeOperatorBson

/**
  * @author Evgenii Kiiski 
  */
trait Logical {

  /**
    * $and	Joins query clauses with a logical AND returns all documents that match the conditions of both clauses.
    */
  final def and(b1: BsonValue, b2: BsonValue): BsonDocument =
    new BsonDocument("$and", BsonArray(b1, b2))
  final def and(b1: BsonValue, b2: BsonValue, b3: BsonValue): BsonDocument =
    new BsonDocument("$and", BsonArray(b1, b2, b3))
  final def and(b1: BsonValue, b2: BsonValue, b3: BsonValue, b4: BsonValue): BsonDocument =
    new BsonDocument("$and", BsonArray(b1, b2, b3, b4))
  final def and(
    b1: BsonValue,
    b2: BsonValue,
    b3: BsonValue,
    b4: BsonValue,
    b5: BsonValue
  ): BsonDocument = new BsonDocument("$and", BsonArray(b1, b2, b3, b4, b5))
  final def and(
    b1: BsonValue,
    b2: BsonValue,
    b3: BsonValue,
    b4: BsonValue,
    b5: BsonValue,
    b6: BsonValue
  ): BsonDocument = new BsonDocument("$and", BsonArray(b1, b2, b3, b4, b5, b6))
  final def and(bs: Seq[BsonValue]): BsonDocument = new BsonDocument("$and", BsonArray(bs))

  /**
    * $not	Inverts the effect of a query expression and returns documents that do not match the query expression.
    */
  final def not(key: String, cond: BsonValue): BsonDocument =
    includeOperatorBson(key, "$not", cond)
  /**
    * $nor	Joins query clauses with a logical NOR returns all documents that fail to match both clauses.
    */
  final def nor(b1: BsonValue, b2: BsonValue): BsonDocument =
    new BsonDocument("$nor", BsonArray(b1, b2))
  final def nor(b1: BsonValue, b2: BsonValue, b3: BsonValue): BsonDocument =
    new BsonDocument("$nor", BsonArray(b1, b2, b3))
  final def nor(b1: BsonValue, b2: BsonValue, b3: BsonValue, b4: BsonValue): BsonDocument =
    new BsonDocument("$nor", BsonArray(b1, b2, b3, b4))
  final def nor(
    b1: BsonValue,
    b2: BsonValue,
    b3: BsonValue,
    b4: BsonValue,
    b5: BsonValue
  ): BsonDocument = new BsonDocument("$nor", BsonArray(b1, b2, b3, b4, b5))
  final def nor(
    b1: BsonValue,
    b2: BsonValue,
    b3: BsonValue,
    b4: BsonValue,
    b5: BsonValue,
    b6: BsonValue
  ): BsonDocument = new BsonDocument("$nor", BsonArray(b1, b2, b3, b4, b5, b6))
  final def nor(bs: Seq[BsonValue]): BsonDocument = new BsonDocument("$nor", BsonArray(bs))

  /**
    * $or	Joins query clauses with a logical OR returns all documents that match the conditions of either clause.
    */
  final def or(b1: BsonValue, b2: BsonValue): BsonDocument =
    new BsonDocument("$or", BsonArray(b1, b2))
  final def or(b1: BsonValue, b2: BsonValue, b3: BsonValue): BsonDocument =
    new BsonDocument("$or", BsonArray(b1, b2, b3))
  final def or(b1: BsonValue, b2: BsonValue, b3: BsonValue, b4: BsonValue): BsonDocument =
    new BsonDocument("$or", BsonArray(b1, b2, b3, b4))
  final def or(
    b1: BsonValue,
    b2: BsonValue,
    b3: BsonValue,
    b4: BsonValue,
    b5: BsonValue
  ): BsonDocument = new BsonDocument("$or", BsonArray(b1, b2, b3, b4, b5))
  final def or(
    b1: BsonValue,
    b2: BsonValue,
    b3: BsonValue,
    b4: BsonValue,
    b5: BsonValue,
    b6: BsonValue
  ): BsonDocument = new BsonDocument("$or", BsonArray(b1, b2, b3, b4, b5, b6))
  final def or(bs: Seq[BsonValue]): BsonDocument = new BsonDocument("$or", BsonArray(bs))

}
