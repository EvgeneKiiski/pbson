package pbson.encoder

import org.bson.{ BsonDocument, BsonValue }
import pbson.BsonEncoder
import pbson.utils.AnyValUtils
import shapeless._


/**
  * @author Evgenii Kiiski 
  */
abstract class DerivedBsonEncoder[A] extends BsonEncoder[A]

object DerivedBsonEncoder extends DerivedBsonEncoderInstances {

  @inline final def apply[A](implicit e: DerivedBsonEncoder[A]): DerivedBsonEncoder[A] = e

}

trait DerivedBsonEncoderInstances extends MidPriorityDerivedBsonEncoderInstances with AnyValUtils {

  implicit final def deriveWrappedEncoder[A <: AnyVal, R, U](implicit
    gen: Generic.Aux[A, R],
    avh: AnyValHelper.Aux[R, U],
    encode: Lazy[BsonEncoder[U]]
  ): DerivedBsonEncoder[A] = new DerivedBsonEncoder[A] {
    final def apply(t: A): BsonValue = encode.value(avh.unwrap(gen.to(t)))
  }

}

trait MidPriorityDerivedBsonEncoderInstances extends LowPriorityDerivedBsonEncoderInstances {

  implicit final def deriveProductEncoder[A, R](implicit
    gen: LabelledGeneric.Aux[A, R],
    encode: Lazy[ReprBsonProductEncoder[R]]
  ): DerivedBsonEncoder[A] = new DerivedBsonEncoder[A] {
    final def apply(t: A): BsonValue = encode.value.apply(new BsonDocument(), gen.to(t))
  }

}

trait LowPriorityDerivedBsonEncoderInstances {


  implicit final def deriveCoproductEncoder[A, R](implicit
    gen: LabelledGeneric.Aux[A, R],
    encode: Lazy[ReprBsonCoproductEncoder[R]]
  ): DerivedBsonEncoder[A] = new DerivedBsonEncoder[A] {
    final def apply(t: A): BsonValue = encode.value.apply(gen.to(t))
  }

}
