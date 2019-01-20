package pbson

import org.mongodb.scala.bson._

/**
  * @author Evgenii Kiiski 
  */
abstract class BsonEncoder[A] {
  def apply(t: A): BsonValue
}

object BsonEncoder {

  @inline final def apply[A](implicit e: BsonEncoder[A]): BsonEncoder[A] = e

  implicit final val unitEncoder: BsonEncoder[Unit] = _ => BsonNull()

  implicit final val stringEncoder: BsonEncoder[String] = BsonString(_)

  implicit final val charEncoder: BsonEncoder[Char] = c => BsonString(String.valueOf(c))

  implicit final val shortEncoder: BsonEncoder[Short] = BsonInt32(_)

  implicit final val intEncoder: BsonEncoder[Int] = BsonInt32(_)

  implicit final val longEncoder: BsonEncoder[Long] = BsonInt64(_)

  implicit final val doubleEncoder: BsonEncoder[Double] = BsonDouble(_)

  implicit final val floatEncoder: BsonEncoder[Float] = f => BsonDouble(f.toDouble)

  implicit final val booleanEncoder: BsonEncoder[Boolean] = BsonBoolean(_)

  implicit final def optionEncoder[A](implicit e: BsonEncoder[A]): BsonEncoder[Option[A]] = {
    case Some(v) => e(v)
    case None => BsonNull()
  }

  implicit final def seqEncoder[A](implicit e: BsonEncoder[A]): BsonEncoder[Seq[A]] = t =>
    if (t.isEmpty) {
      BsonNull()
    } else {
      BsonArray.apply(t.map(e.apply))
    }

  implicit final def mapEncoderDocument[K, V](implicit e: BsonMapEncoder[K, V]): BsonEncoder[Map[K, V]] = t =>
    if (t.isEmpty) {
      BsonNull()
    } else {
      BsonDocument(t.map(e.apply))
    }

}
