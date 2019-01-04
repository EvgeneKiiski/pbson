package pbson.decoder

import org.bson.BsonArray
import org.mongodb.scala.bson.BsonValue
import pbson.{BsonDecoder, BsonError}
import collection.JavaConverters._
import cats._
import cats.implicits._


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

  implicit final def optionDecoder[A](implicit d: BsonDecoder[A]): BsonDecoder[Option[A]] = {
    case null => Right(None)
    case b => d(b).map(Some.apply)
  }

  implicit final def seqDecoder[A](implicit d: BsonDecoder[A]): BsonDecoder[Seq[A]] = {
    case null => Right(Seq.empty)
    case b: BsonValue if b.isArray =>
      val seq: List[BsonValue] = b.asArray().getValues.asScala.toList
      seq.traverse(d.apply)
  }

}
