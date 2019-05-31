package pbson.decoder

import org.bson.BsonType
import pbson.BsonError.UnexpectedType
import pbson.utils.AnyValUtils
import pbson.{ BsonDecoder, BsonError }
import shapeless._

/**
  * @author Evgenii Kiiski 
  */
abstract class DerivedBsonDecoder[A] extends BsonDecoder[A]

object DerivedBsonDecoder extends DerivedBsonDecoderInstances {

  @inline final def apply[A](implicit e: DerivedBsonDecoder[A]): DerivedBsonDecoder[A] = e

}

trait DerivedBsonDecoderInstances extends LowPriorityDerivedBsonDecoderInstances with AnyValUtils {

  implicit final def deriveWrappedDecoder[A <: AnyVal, R, U](implicit
    gen: Generic.Aux[A, R],
    avh: AnyValHelper.Aux[R, U],
    decode: Lazy[BsonDecoder[U]]
  ): DerivedBsonDecoder[A] = b => decode.value(b).map(v => gen.from(avh.wrap(v)))

}

trait LowPriorityDerivedBsonDecoderInstances {
  implicit final def deriveDecoder[A, R, K](implicit
    gen: LabelledGeneric.Aux[A, R],
    decode: Lazy[ReprBsonDecoder[R]]
  ): DerivedBsonDecoder[A] = b => {
    if (b.isDocument) {
      decode.value(b.asDocument()) match {
        case Right(r) => Right(gen.from(r))
        case l @ Left(_) => l.asInstanceOf[Either[BsonError, A]]
      }
    } else {
      Left(UnexpectedType(b, BsonType.DOCUMENT))
    }
  }
}