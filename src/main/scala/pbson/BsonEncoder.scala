package pbson

import java.util

import org.bson._
import pbson.encoder.DerivedBsonEncoder
import shapeless.Lazy

import scala.collection.JavaConverters._
import scala.collection.immutable

/**
  * @author Evgenii Kiiski 
  */
abstract class BsonEncoder[A] {
  def apply(t: A): BsonValue
}

object BsonEncoder extends BsonEncoderInstances {

  @inline final def apply[A](implicit e: BsonEncoder[A]): BsonEncoder[A] = e

  private[pbson] val BSON_NULL = new BsonNull()

}

trait BsonEncoderInstances extends LowPriorityBsonEncoderInstances {

  implicit final val unitEncoder: BsonEncoder[Unit] = _ => BsonEncoder.BSON_NULL

  implicit final val stringEncoder: BsonEncoder[String] = s => new BsonString(s)

  implicit final val charEncoder: BsonEncoder[Char] = c => new BsonString(String.valueOf(c))

  implicit final val shortEncoder: BsonEncoder[Short] = s => new BsonInt32(s)

  implicit final val intEncoder: BsonEncoder[Int] = i => new BsonInt32(i)

  implicit final val longEncoder: BsonEncoder[Long] = l => new BsonInt64(l)

  implicit final val doubleEncoder: BsonEncoder[Double] = d => new BsonDouble(d)

  implicit final val floatEncoder: BsonEncoder[Float] = f => new BsonDouble(f.toDouble)

  implicit final val booleanEncoder: BsonEncoder[Boolean] = b => new BsonBoolean(b)

  implicit final def optionEncoder[A](implicit e: BsonEncoder[A]): BsonEncoder[Option[A]] = {
    case Some(v) => e(v)
    case None => BsonEncoder.BSON_NULL
  }

  implicit final def seqEncoder[A](implicit e: BsonEncoder[A]): BsonEncoder[Seq[A]] = t =>
    if (t.isEmpty) {
      BsonEncoder.BSON_NULL
    } else {
      new BsonArray(t.map(e.apply).asJava)
    }

  implicit final def listEncoder[A](implicit e: BsonEncoder[A]): BsonEncoder[List[A]] = t =>
    if (t.isEmpty) {
      BsonEncoder.BSON_NULL
    } else {
      new BsonArray(t.map(e.apply).asJava)
    }

  implicit final def setEncoder[A](implicit e: BsonEncoder[A]): BsonEncoder[Set[A]] = t =>
    if (t.isEmpty) {
      BsonEncoder.BSON_NULL
    } else {
      val set: util.Set[BsonValue] = t.map(e.apply).asJava
      val list = new util.ArrayList[BsonValue](set)
      new BsonArray(list)
    }

  implicit final def vectorEncoder[A](implicit e: BsonEncoder[A]): BsonEncoder[Vector[A]] = t =>
    if (t.isEmpty) {
      BsonEncoder.BSON_NULL
    } else {
      new BsonArray(t.map(e.apply).asJava)
    }

  implicit final def mapEncoderDocument[K, V](implicit
                                              e: BsonMapEncoder[K, V]
                                             ): BsonEncoder[Map[K, V]] = t =>
    if (t.isEmpty) {
      BsonEncoder.BSON_NULL
    } else {
      val doc = new BsonDocument()
      doc.putAll(t.map(e.apply).asJava)
      doc
    }

}

trait LowPriorityBsonEncoderInstances {

  implicit final def deriveEncoder[A](implicit encode: Lazy[DerivedBsonEncoder[A]]): BsonEncoder[A] = encode.value

}
