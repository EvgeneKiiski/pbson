package pbson

import pbson.encoder.DerivedBsonEncoder
import shapeless.Lazy

/**
  * @author Evgenii Kiiski 
  */
object semiauto {

  final def deriveEncoder[A](implicit encode: DerivedBsonEncoder[A]): BsonEncoder[A] = encode

}
