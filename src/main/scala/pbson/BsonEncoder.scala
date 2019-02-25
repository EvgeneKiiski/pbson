package pbson

import java.util
import java.util.UUID

import org.bson._
import pbson.BsonEncoder.BSON_NULL
import pbson.encoder.DerivedBsonEncoder
import shapeless.Lazy

import scala.collection.JavaConverters._
import scala.collection.immutable

/**
  * @author Evgenii Kiiski 
  */
abstract class BsonEncoder[A] { self =>
  def apply(t: A): BsonValue

  final def contramap[B](f: B => A): BsonEncoder[B] = (a: B) => self(f(a))

  final def contramapOrNull[B](f: B => A): BsonEncoder[B] = (a: B) =>
    if(a == null) BSON_NULL else self(f(a))
}

object BsonEncoder extends BsonEncoderInstances {

  @inline final def apply[A](implicit e: BsonEncoder[A]): BsonEncoder[A] = e

  private[pbson] val BSON_NULL = new BsonNull()

}

trait BsonEncoderInstances extends LowPriorityBsonEncoderInstances {

  import BsonEncoder._

  implicit final val unitEncoder: BsonEncoder[Unit] = _ => BSON_NULL

  implicit final val stringEncoder: BsonEncoder[String] = s =>
    if(s == null) BSON_NULL else new BsonString(s)

  implicit final val charEncoder: BsonEncoder[Char] = c => new BsonString(String.valueOf(c))

  implicit final val javaCharEncoder: BsonEncoder[java.lang.Character] =
    charEncoder.contramapOrNull(_.charValue())

  implicit final val shortEncoder: BsonEncoder[Short] = s => new BsonInt32(s)

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

  implicit final val uuidEncoder: BsonEncoder[UUID] = u => new BsonString(u.toString)

  implicit final def optionEncoder[A](implicit e: BsonEncoder[A]): BsonEncoder[Option[A]] = {
    case Some(v) => e(v)
    case None => BSON_NULL
  }

  implicit final def seqEncoder[A](implicit e: BsonEncoder[A]): BsonEncoder[Seq[A]] = t =>
    if (t.isEmpty) {
      BSON_NULL
    } else {
      new BsonArray(t.map(e.apply).asJava)
    }

  implicit final def listEncoder[A](implicit e: BsonEncoder[A]): BsonEncoder[List[A]] = t =>
    if (t.isEmpty) {
      BSON_NULL
    } else {
      new BsonArray(t.map(e.apply).asJava)
    }

  implicit final def setEncoder[A](implicit e: BsonEncoder[A]): BsonEncoder[Set[A]] = t =>
    if (t.isEmpty) {
      BSON_NULL
    } else {
      val set: util.Set[BsonValue] = t.map(e.apply).asJava
      val list = new util.ArrayList[BsonValue](set)
      new BsonArray(list)
    }

  implicit final def vectorEncoder[A](implicit e: BsonEncoder[A]): BsonEncoder[Vector[A]] = t =>
    if (t.isEmpty) {
      BSON_NULL
    } else {
      new BsonArray(t.map(e.apply).asJava)
    }

  implicit final def mapEncoderDocument[K, V](implicit
                                              e: BsonMapEncoder[K, V]
                                             ): BsonEncoder[Map[K, V]] = t =>
    if (t.isEmpty) {
      BSON_NULL
    } else {
      val doc = new BsonDocument()
      doc.putAll(t.map(e.apply).asJava)
      doc
    }

}

trait LowPriorityBsonEncoderInstances {

  implicit final def deriveEncoder[A](implicit encode: Lazy[DerivedBsonEncoder[A]]): BsonEncoder[A] = encode.value

}
