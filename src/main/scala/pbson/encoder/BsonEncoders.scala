package pbson.encoder

import org.mongodb.scala.bson.{BsonBoolean, BsonInt32, BsonInt64, BsonNull, BsonString}
import pbson.BsonEncoder

import scala.language.implicitConversions

/**
  * @author Evgenii Kiiski 
  */
trait BsonEncoders {

  implicit val stringEncoder: BsonEncoder[String] = BsonString(_)

  implicit val intEncoder: BsonEncoder[Int] = BsonInt32(_)

  implicit val longEncoder: BsonEncoder[Long] = BsonInt64(_)

  implicit val booleanEncoder: BsonEncoder[Boolean] = BsonBoolean(_)

  implicit final def optionEncoder[A](implicit encoder: BsonEncoder[A]): BsonEncoder[Option[A]] = {
    case Some(v) => encoder(v)
    case None => BsonNull()
  }

}
