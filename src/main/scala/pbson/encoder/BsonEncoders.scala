package pbson.encoder

import org.mongodb.scala.bson.{BsonArray, BsonBoolean, BsonDocument, BsonInt32, BsonInt64, BsonNull, BsonString, BsonValue}
import pbson.{BsonEncoder, BsonMapEncoder}

import scala.language.{higherKinds, implicitConversions}

/**
  * @author Evgenii Kiiski 
  */
trait BsonEncoders {

  implicit final val stringEncoder: BsonEncoder[String] = BsonString(_)

  implicit final val intEncoder: BsonEncoder[Int] = BsonInt32(_)

  implicit final val longEncoder: BsonEncoder[Long] = BsonInt64(_)

  implicit final val booleanEncoder: BsonEncoder[Boolean] = BsonBoolean(_)

  implicit final def optionEncoder[A](implicit e: BsonEncoder[A]): BsonEncoder[Option[A]] = {
    case Some(v) => e(v)
    case None => BsonNull()
  }

  implicit final def seqEncoder[A](implicit e: BsonEncoder[A]): BsonEncoder[Seq[A]] =
    t => BsonArray.apply(t.map(e.apply))

  implicit final def kvMapEncoder[K, V](implicit ke: BsonEncoder[K], ve: BsonEncoder[V]): BsonMapEncoder[K, V] = {
    case (k, v) => BsonDocument("k" -> ke(k), "v" -> ve(v))
  }

  implicit final def mapEncoder[K, V](implicit e: BsonMapEncoder[K, V]): BsonEncoder[Map[K, V]] =
    t => BsonArray.apply(t.map(e.apply))

}
