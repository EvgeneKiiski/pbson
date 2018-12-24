package pbson.decoder

import org.mongodb.scala.bson.BsonValue
import pbson.{BsonDecoder, BsonError}

/**
  * @author Evgenii Kiiski 
  */
trait BsonDecoders {

  implicit val stringDecoder: BsonDecoder[BsonError, String] = (b: BsonValue) =>
    Either.cond(
      b.isString,
      b.asString().getValue,
      BsonError.InvalidType(s"Invalid type: ${b.getBsonType} expected: String")
    )

  implicit val intDecoder: BsonDecoder[BsonError, Int] = (b: BsonValue) => {
    if(b.isInt32) {
      Right(b.asInt32().getValue)
    } else if(b.isInt64) {
      Right(b.asInt64().intValue())
    } else {
      Left(BsonError.InvalidType(s"Invalid type: ${b.getBsonType} expected: Int"))
    }
  }

  implicit val longDecoder: BsonDecoder[BsonError, Long] = (b: BsonValue) => {
    if(b.isInt32) {
      Right(b.asInt32().longValue())
    } else if(b.isInt64) {
      Right(b.asInt64().getValue)
    } else {
      Left(BsonError.InvalidType(s"Invalid type: ${b.getBsonType} expected: Int"))
    }
  }

  implicit val booleanDecoder: BsonDecoder[BsonError, Boolean] = (b: BsonValue) =>
    Either.cond(
      b.isBoolean,
      b.asBoolean().getValue,
      BsonError.InvalidType(s"Invalid type: ${b.getBsonType} expected: Boolean")
    )

  def optionDecoder[A](decoder: BsonDecoder[BsonError, A]): BsonDecoder[BsonError, Option[A]] = {
    case null => Right(None)
    case b => decoder(b).map(Some.apply)
  }

}
