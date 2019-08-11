package pbson

import java.util.UUID

import org.bson._
import org.bson.types.Decimal128
import pbson.BsonEncoder.BSON_NULL
import pbson.encoder.DerivedBsonEncoder
import shapeless.Lazy


/**
  * @author Evgenii Kiiski 
  */
object BsonEncoder {

  implicit class BsonEncoderOps[A](val encoder: BsonEncoder[A]) extends AnyVal {
    @inline final def contramapOrNull[B](f: B => A): BsonEncoder[B] = (a: B) =>
      if (a == null) BSON_NULL else encoder.contramap(f)(a)
  }

  @inline final def apply[A](implicit e: BsonEncoder[A]): BsonEncoder[A] = e

  private[pbson] final val BSON_NULL = new BsonNull()
}

trait BsonEncoderInstances extends LowPriorityBsonEncoderInstances {

  import BsonEncoder._

  implicit final val unitEncoder: BsonEncoder[Unit] = _ => BSON_NULL

  implicit final val stringEncoder: BsonEncoder[String] = s =>
    if (s == null) BSON_NULL else new BsonString(s)

  implicit final val charEncoder: BsonEncoder[Char] = c => new BsonString(String.valueOf(c))

  implicit final val javaCharEncoder: BsonEncoder[java.lang.Character] =
    charEncoder.contramapOrNull(_.charValue())

  implicit final val shortEncoder: BsonEncoder[Short] = s => new BsonInt32(s.toInt)

  implicit final val javaShortEncoder: BsonEncoder[java.lang.Short] =
    shortEncoder.contramapOrNull(_.shortValue())

  implicit final val intEncoder: BsonEncoder[Int] = i => new BsonInt32(i)

  implicit final val javaIntEncoder: BsonEncoder[java.lang.Integer] =
    intEncoder.contramapOrNull(_.intValue())

  implicit final val longEncoder: BsonEncoder[Long] = l => new BsonInt64(l)

  implicit final val javaLongEncoder: BsonEncoder[java.lang.Long] =
    longEncoder.contramapOrNull(_.longValue())

  implicit final val doubleEncoder: BsonEncoder[Double] = d => new BsonDouble(d)

  implicit final val javaDoubleEncoder: BsonEncoder[java.lang.Double] =
    doubleEncoder.contramapOrNull(_.doubleValue())

  implicit final val floatEncoder: BsonEncoder[Float] = f => new BsonDouble(f.toDouble)

  implicit final val javaFloatEncoder: BsonEncoder[java.lang.Float] =
    floatEncoder.contramapOrNull(_.floatValue())

  implicit final val booleanEncoder: BsonEncoder[Boolean] = b => new BsonBoolean(b)

  implicit final val javaBooleanEncoder: BsonEncoder[java.lang.Boolean] =
    booleanEncoder.contramapOrNull(_.booleanValue())

  implicit final val uuidEncoder: BsonEncoder[UUID] = u => new BsonBinary(u)

  implicit final val decimal128Encoder: BsonEncoder[Decimal128] = s =>
    if (s == null) BSON_NULL else new BsonDecimal128(s)

  implicit final val javaDateEncoder: BsonEncoder[java.util.Date] = d =>
    if (d == null) BSON_NULL else new BsonDateTime(d.getTime)

}

trait LowPriorityBsonEncoderInstances {

  implicit final def deriveEncoderLow[A](implicit
    encode: Lazy[DerivedBsonEncoder[A]]
  ): BsonEncoder[A] = encode.value
}
