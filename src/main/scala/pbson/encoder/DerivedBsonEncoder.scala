package pbson.encoder

import org.bson.{ BsonDocument, BsonValue }
import pbson.{ BsonEncoder, Encoder }
import pbson.utils.AnyValUtils
import shapeless._


/**
  * @author Evgenii Kiiski 
  */
abstract class DerivedBsonEncoder[A] extends Encoder[BsonValue, A]

object DerivedBsonEncoder extends DerivedBsonEncoderInstances {

  @inline final def apply[A](implicit e: DerivedBsonEncoder[A]): DerivedBsonEncoder[A] = e
}

trait DerivedBsonEncoderInstances extends MidPriorityDerivedBsonEncoderInstances with AnyValUtils {

  implicit final def deriveWrappedEncoder[A <: AnyVal, R, U](implicit
    gen: Generic.Aux[A, R],
    avh: AnyValHelper.Aux[R, U],
    encode: Lazy[BsonEncoder[U]]
  ): DerivedBsonEncoder[A] = a => encode.value(avh.unwrap(gen.to(a)))
}

trait MidPriorityDerivedBsonEncoderInstances extends LowPriorityDerivedBsonEncoderInstances {

  implicit final def deriveProductEncoder[A, R](implicit
    gen: LabelledGeneric.Aux[A, R],
    encode: Lazy[ReprBsonProductEncoder[R]]
  ): DerivedBsonEncoder[A] = a => encode.value.apply(new BsonDocument(), gen.to(a))
}

trait LowPriorityDerivedBsonEncoderInstances {

  implicit final def deriveCoproductEncoder[A, R](implicit
    gen: LabelledGeneric.Aux[A, R],
    encode: Lazy[ReprBsonCoproductEncoder[R]]
  ): DerivedBsonEncoder[A] = a => encode.value.apply(gen.to(a))
}
