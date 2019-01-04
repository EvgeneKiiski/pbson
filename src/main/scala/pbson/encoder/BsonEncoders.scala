package pbson.encoder

import org.mongodb.scala.bson.{BsonArray, BsonBoolean, BsonInt32, BsonInt64, BsonNull, BsonString, BsonValue}
import pbson.BsonEncoder

import scala.language.{higherKinds, implicitConversions}

/**
  * @author Evgenii Kiiski 
  */
trait BsonEncoders {

  implicit val stringEncoder: BsonEncoder[String] = BsonString(_)

  implicit val intEncoder: BsonEncoder[Int] = BsonInt32(_)

  implicit val longEncoder: BsonEncoder[Long] = BsonInt64(_)

  implicit val booleanEncoder: BsonEncoder[Boolean] = BsonBoolean(_)

  implicit final def optionEncoder[A](implicit e: BsonEncoder[A]): BsonEncoder[Option[A]] = {
    case Some(v) => e(v)
    case None => BsonNull()
  }

  implicit final def seqEncoder[A](implicit e: BsonEncoder[A]): BsonEncoder[Seq[A]] =
    t => BsonArray.apply(t.map(e(_)))

}
