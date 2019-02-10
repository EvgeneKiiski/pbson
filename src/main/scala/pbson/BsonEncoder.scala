package pbson

import org.mongodb.scala.bson._
import pbson.BsonEncoder.BSON_NULL
import pbson.BsonError.UnexpectedType
import pbson.encoder.DerivedBsonEncoder
import shapeless.Lazy

/**
  * @author Evgenii Kiiski 
  */
abstract class BsonEncoder[A] {
  def apply(t: A): BsonValue
}

object BsonEncoder extends BsonEncoderInstances {

  @inline final def apply[A](implicit e: BsonEncoder[A]): BsonEncoder[A] = e

  private[pbson] val BSON_NULL = BsonNull()

}

trait BsonEncoderInstances extends LowPriorityBsonEncoderInstances {

  implicit final val unitEncoder: BsonEncoder[Unit] = _ => BsonEncoder.BSON_NULL

  implicit final val stringEncoder: BsonEncoder[String] = BsonString.apply

  implicit final val charEncoder: BsonEncoder[Char] = c => BsonString(String.valueOf(c))

  implicit final val shortEncoder: BsonEncoder[Short] = BsonInt32(_)

  implicit final val intEncoder: BsonEncoder[Int] = BsonInt32.apply

  implicit final val longEncoder: BsonEncoder[Long] = BsonInt64.apply

  implicit final val doubleEncoder: BsonEncoder[Double] = BsonDouble.apply

  implicit final val floatEncoder: BsonEncoder[Float] = f => BsonDouble(f.toDouble)

  implicit final val booleanEncoder: BsonEncoder[Boolean] = BsonBoolean.apply

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

  implicit final def listEncoder[A](implicit e: BsonEncoder[A]): BsonEncoder[List[A]] = t =>
    if (t.isEmpty) {
      BsonNull()
    } else {
      BsonArray.apply(t.map(e.apply))
    }

  implicit final def setEncoder[A](implicit e: BsonEncoder[A]): BsonEncoder[Set[A]] = t =>
    if (t.isEmpty) {
      BsonNull()
    } else {
      BsonArray.apply(t.map(e.apply))
    }

  implicit final def vectorEncoder[A](implicit e: BsonEncoder[A]): BsonEncoder[Vector[A]] = t =>
    if (t.isEmpty) {
      BsonNull()
    } else {
      BsonArray.apply(t.map(e.apply))
    }

  implicit final def mapEncoderDocument[K, V](implicit
                                              e: BsonMapEncoder[K, V]
                                             ): BsonEncoder[Map[K, V]] = t =>
    if (t.isEmpty) {
      BsonNull()
    } else {
      BsonDocument(t.map(e.apply))
    }

}

trait LowPriorityBsonEncoderInstances {

  implicit final def deriveEncoder[A](implicit encode: Lazy[DerivedBsonEncoder[A]]): BsonEncoder[A] = encode.value

}
