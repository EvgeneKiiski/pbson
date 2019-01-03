package pbson.decoder

import org.mongodb.scala.bson.BsonValue
import pbson.{BsonDecoder, BsonError}

import scala.language.implicitConversions

/**
  * @author Evgenii Kiiski 
  */
trait BsonDecoders {

  implicit val stringDecoder: BsonDecoder[String] =
    b => Either.cond(
      b.isString,
      b.asString().getValue,
      BsonError.InvalidType(s"${b.getBsonType} expected: String")
    )

  implicit val intDecoder: BsonDecoder[Int] =
    b => {
      if (b.isInt32) {
        Right(b.asInt32().getValue)
      } else if (b.isInt64) {
        Right(b.asInt64().intValue())
      } else {
        Left(BsonError.InvalidType(s" ${b.getBsonType} expected: Int"))
      }
    }

  implicit val longDecoder: BsonDecoder[Long] =
    b => {
      if (b.isInt32) {
        Right(b.asInt32().longValue())
      } else if (b.isInt64) {
        Right(b.asInt64().getValue)
      } else {
        Left(BsonError.InvalidType(s" ${b.getBsonType} expected: Int"))
      }
    }

  implicit val booleanDecoder: BsonDecoder[Boolean] =
    b => Either.cond(
      b.isBoolean,
      b.asBoolean().getValue,
      BsonError.InvalidType(s"${b.getBsonType} expected: Boolean")
    )

  implicit final def optionDecoder[A](implicit decoder: BsonDecoder[A]): BsonDecoder[Option[A]] = {
    case null => Right(None)
    case b => decoder(b).map(Some.apply)
  }

}
