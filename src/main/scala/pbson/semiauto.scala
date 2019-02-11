package pbson

import org.mongodb.scala.bson.{ BsonArray, BsonNull, BsonString, BsonValue }
import pbson.BsonError.UnexpectedType
import pbson.decoder.DerivedBsonDecoder
import pbson.encoder.DerivedBsonEncoder
import shapeless._


/**
  * @author Evgenii Kiiski 
  */
object semiauto {

  final def deriveEncoder[A](implicit encode: Lazy[DerivedBsonEncoder[A]]): BsonEncoder[A] = encode.value

  final def deriveDecoder[A](implicit decode: Lazy[DerivedBsonDecoder[A]]): BsonDecoder[A] = decode.value

  final def validateDeriveDecoder[A](validator: A => BsonDecoder.Result[A])(implicit
    decode: Lazy[DerivedBsonDecoder[A]]
  ): BsonDecoder[A] = bsonValidate(decode.value, validator)

  final def bsonValidate[A](decoder: BsonDecoder[A], validator: A => BsonDecoder.Result[A]): BsonDecoder[A] =
    (b: BsonValue) => decoder.apply(b).flatMap(validator)

}
