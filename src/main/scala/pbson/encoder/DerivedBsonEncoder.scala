package pbson.encoder

import org.mongodb.scala.bson.{BsonDocument, BsonNull, BsonValue}
import pbson.BsonEncoder
import pbson.utils.AnyValUtils
import shapeless._

import scala.language.experimental.macros

/**
  * @author Evgenii Kiiski 
  */
abstract class DerivedBsonEncoder[A] extends BsonEncoder[A]

object DerivedBsonEncoder extends DerivedBsonEncoderInstances {

  @inline final def apply[A](implicit e: DerivedBsonEncoder[A]): DerivedBsonEncoder[A] = e

}

trait DerivedBsonEncoderInstances extends LowPriorityDerivedBsonEncoderInstances with AnyValUtils {

  implicit final def deriveWrappedEncoder[A <: AnyVal, R, U](implicit
                                                             gen: Generic.Aux[A, R],
                                                             avh: AnyValHelper.Aux[R, U],
                                                             encode: Lazy[BsonEncoder[U]]
                                                            ): DerivedBsonEncoder[A] = new DerivedBsonEncoder[A] {
    override def apply(t: A): BsonValue = encode.value(avh.unwrap(gen.to(t)))
  }

}

trait LowPriorityDerivedBsonEncoderInstances {

  implicit final def deriveEncoder[A, R](implicit
                                         gen: LabelledGeneric.Aux[A, R],
                                         encode: Lazy[ReprBsonEncoder[R]]
                                        ): DerivedBsonEncoder[A] = new DerivedBsonEncoder[A] {
    final def apply(t: A): BsonValue = {
      BsonDocument(
        encode.value.apply(gen.to(t)).filterNot {
          case (_, v) if v == BsonNull() => true
          case _ => false
        }
      )
    }
  }

}
