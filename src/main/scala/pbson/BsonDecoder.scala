package pbson

import org.mongodb.scala.bson.{BsonNull, BsonValue}
import pbson.BsonError.{BsonIsNull, InvalidType}

import collection.JavaConverters._
import cats._
import cats.implicits._

/**
  * @author Evgenii Kiiski 
  */
trait BsonDecoder[A] {
  def apply(b: BsonValue): BsonDecoder.Result[A]
}

object BsonDecoder {

  type Result[A] = Either[BsonError, A]

  @inline final def apply[A](implicit d: BsonDecoder[A]): BsonDecoder[A] = d

  private[this] def instance[A](f: BsonValue => Result[A]): BsonDecoder[A] = f(_)

  implicit final val unitDecoder: BsonDecoder[Unit] =
    b => Either.cond(
      b == BsonNull(),
      Unit,
      BsonError.InvalidType(s"${b.getBsonType} expected: BsonNull")
    )

  implicit final val stringDecoder: BsonDecoder[String] =
    b => Either.cond(
      b.isString,
      b.asString().getValue,
      BsonError.InvalidType(s"${b.getBsonType} expected: String")
    )

  implicit final val charDecoder: BsonDecoder[Char] =
    b => Either.cond(
      b.isString && b.asString().getValue.length == 1,
      b.asString().getValue.head,
      BsonError.InvalidType(s"${b.getBsonType} expected: String length 1")
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

  implicit final val shortDecoder: BsonDecoder[Short] =
    b => {
      if (b.isInt32) {
        Right(b.asInt32().intValue().toShort)
      } else if (b.isInt64) {
        Right(b.asInt64().intValue().toShort)
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

  implicit final val doubleDecoder: BsonDecoder[Double] =
    b => Either.cond(
      b.isDouble,
      b.asDouble().getValue,
      BsonError.InvalidType(s"${b.getBsonType} expected: Double")
    )

  implicit final val floatDecoder: BsonDecoder[Float] =
    b => Either.cond(
      b.isDouble,
      b.asDouble().getValue.toFloat,
      BsonError.InvalidType(s"${b.getBsonType} expected: Double")
    )

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
        kd(key).flatMap(k => vd(value).map(v => (k, v)))
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
