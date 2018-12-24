package pbson.encoder

import org.mongodb.scala.bson.{BsonBoolean, BsonInt32, BsonInt64, BsonNull, BsonString}
import pbson.BsonEncoder

/**
  * @author Evgenii Kiiski 
  */
trait BsonEncoders {

  implicit val stringEncoder: BsonEncoder[String] = (t: String) => BsonString(t)

  implicit val intEncoder: BsonEncoder[Int] = (t: Int) => BsonInt32(t)

  implicit val longEncoder: BsonEncoder[Long] = (t: Long) => BsonInt64(t)

  implicit val booleanEncoder: BsonEncoder[Boolean] = (t: Boolean) => BsonBoolean(t)

  def optionEncoder[A](encoder: BsonEncoder[A]): BsonEncoder[Option[A]] = {
    case Some(v: A) => encoder(v)
    case None => BsonNull()
  }

}
