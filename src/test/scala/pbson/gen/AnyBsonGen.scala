package pbson.gen

import java.util.Date

import org.bson.{ BsonType, BsonValue }
import org.mongodb.scala.bson._
import org.scalacheck.Gen
import pbson.BsonError

/**
  * @author Evgenii Kiiski
  */
object AnyBsonGen {

  val anyBsonGen: Gen[BsonValue] = Gen.oneOf(
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

  val anyBsonType: Gen[BsonType] = Gen.oneOf(
    BsonType.END_OF_DOCUMENT,
    BsonType.DOUBLE,
    BsonType.STRING,
    BsonType.DOCUMENT,
    BsonType.ARRAY,
    BsonType.BINARY,
    BsonType.UNDEFINED,
    BsonType.OBJECT_ID,
    BsonType.BOOLEAN,
    BsonType.DATE_TIME,
    BsonType.NULL,
    BsonType.REGULAR_EXPRESSION,
    BsonType.DB_POINTER,
    BsonType.JAVASCRIPT,
    BsonType.SYMBOL,
    BsonType.JAVASCRIPT_WITH_SCOPE,
    BsonType.INT32,
    BsonType.TIMESTAMP,
    BsonType.INT64,
    BsonType.DECIMAL128,
    BsonType.MIN_KEY,
    BsonType.MAX_KEY
  )

  val anyErrorGen: Gen[BsonError] =
    for {
      bson <- anyBsonGen
      typ <- anyBsonType
      str <- Gen.alphaChar
      result <- Gen.oneOf[BsonError](
        BsonError.Error,
        BsonError.UnexpectedEmptyString,
        BsonError.ADTValueNotFound,
        BsonError.UnexpectedType(bson, typ),
        BsonError.UnexpectedValue(bson),
        BsonError.WrappedThrowable(new RuntimeException("")),
        BsonError.FieldNotFound(str.toString),
        BsonError.ValidateError(str.toString),
        BsonError.ArrayValueNotFound()
      )
    } yield result

}
