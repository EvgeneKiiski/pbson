package pbson.decoder

import org.bson.BsonArray
import org.mongodb.scala.bson.BsonValue
import pbson.{BsonDecoder, BsonError, BsonMapDecoder}

import collection.JavaConverters._
import cats._
import cats.implicits._
import pbson.BsonDecoder.Result
import pbson.BsonError.InvalidType

import scala.language.implicitConversions

/**
  * @author Evgenii Kiiski 
  */
trait BsonDecoders {

  implicit final val stringDecoder: BsonDecoder[String] =
    b => Either.cond(
      b.isString,
      b.asString().getValue,
      BsonError.InvalidType(s"${b.getBsonType} expected: String")
    )

  implicit final val intDecoder: BsonDecoder[Int] =
    b => {
      if (b.isInt32) {
        Right(b.asInt32().getValue)
      } else if (b.isInt64) {
        Right(b.asInt64().intValue())
      } else {
        Left(BsonError.InvalidType(s" ${b.getBsonType} expected: Int"))
      }
    }

  implicit final val longDecoder: BsonDecoder[Long] =
    b => {
      if (b.isInt32) {
        Right(b.asInt32().longValue())
      } else if (b.isInt64) {
        Right(b.asInt64().getValue)
      } else {
        Left(BsonError.InvalidType(s" ${b.getBsonType} expected: Int"))
      }
    }

  implicit final val booleanDecoder: BsonDecoder[Boolean] =
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

  implicit final def kvMapDecoder[K, V](implicit kd: BsonDecoder[K], vd: BsonDecoder[V]): BsonMapDecoder[K, V] = {
    case b: BsonValue if b.isDocument =>
      val doc = b.asDocument()
      val key = doc.get("k")
      val value = doc.get("v")
      if (key != null && value != null) {
        for {
          k <- kd(key)
          v <- vd(value)
        } yield (k, v)
      } else {
        Left(InvalidType(s" ${doc.toJson} expected k,v"))
      }
    case b => Left(InvalidType(b.toString))
  }

  implicit final def mapDecoder[K, V](implicit d: BsonMapDecoder[K, V]): BsonDecoder[Map[K, V]] = {
    case null => Right(Map.empty)
    case b: BsonValue if b.isArray =>
      val seq: List[BsonValue] = b.asArray().getValues.asScala.toList
      seq.traverse(d.apply).map(_.toMap)
  }

}
