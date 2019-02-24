package pbson

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
  ): BsonDecoder[A] = decode.value.flatMap(a => BsonDecoder.fromEither(validator(a)))

}
