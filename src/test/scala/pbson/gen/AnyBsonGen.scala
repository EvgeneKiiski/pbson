package pbson.gen

import java.util.Date

import org.bson.BsonValue
import org.mongodb.scala.bson._
import org.scalacheck.Gen

/**
  * @author Evgenii Kiiski
  */
object AnyBsonGen {

  val anyGen: Gen[BsonValue] = Gen.oneOf(
    BsonDouble(23.232): BsonValue,
    BsonString("sdsds"): BsonValue,
    BsonDocument(): BsonValue,
    BsonArray(): BsonValue,
    BsonBinary(Array.emptyByteArray): BsonValue,
    BsonUndefined(): BsonValue,
    BsonObjectId(): BsonValue,
    BsonBoolean(true): BsonValue,
    BsonDateTime(new Date()): BsonValue,
    BsonNull(): BsonValue,
    BsonRegularExpression(".*"),
    BsonJavaScript(""),
    BsonSymbol('a),
    BsonJavaScriptWithScope(""),
    BsonInt32(23423),
    BsonTimestamp(),
    BsonInt64(23423),
    BsonDecimal128("234241321.1231"),
    BsonMinKey(),
    BsonMaxKey()
  )


}
