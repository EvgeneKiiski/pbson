package pbson

import org.mongodb.scala.bson._

/**
  * @author Evgenii Kiiski 
  */
trait BsonEncoder[A] {
  def apply(t: A): BsonValue
}

object BsonEncoder {

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
