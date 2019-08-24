package pbson.decoder

import pbson.BsonDecoder.BsonDecoderNotNull
import shapeless.{ Generic, Lazy }
import pbson.utils.AnyValUtils

/**
  * @author Evgenii Kiiski 
  */
abstract class DerivedBsonDecoderNotNull[A] extends BsonDecoderNotNull[A]

object DerivedBsonDecoderNotNull extends AnyValUtils {

  @inline final def apply[A](implicit e: DerivedBsonDecoderNotNull[A]): DerivedBsonDecoderNotNull[A] = e

  implicit final def deriveWrappedDecoderNotNull[A <: AnyVal, R, U](implicit
    gen: Generic.Aux[A, R],
    avh: AnyValHelper.Aux[R, U],
    decode: Lazy[BsonDecoderNotNull[U]]
  ): DerivedBsonDecoderNotNull[A] = b =>
    decode.value(b).map(v => gen.from(avh.wrap(v)))

}
