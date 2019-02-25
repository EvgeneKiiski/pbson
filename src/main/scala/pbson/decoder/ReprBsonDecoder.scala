package pbson.decoder

import org.bson.BsonDocument
import pbson.BsonError.{ADTValueNotFound, FieldNotFound}
import pbson.BsonConst._
import pbson.BsonDecoder.BsonDecoderNotNull
import pbson.{BsonConst, BsonDecoder, BsonError}
import shapeless._
import shapeless.labelled.FieldType


/**
  * @author Evgenii Kiiski 
  */
trait ReprBsonDecoder[R] {
  def apply(b: BsonDocument): ReprBsonDecoder.Result[R]
}

object ReprBsonDecoder extends ReprBsonDecoderInstances {

  type Result[A] = Either[BsonError, A]

  @inline final def apply[R](implicit d: ReprBsonDecoder[R]): ReprBsonDecoder[R] = d

  implicit val hnilDecoder: ReprBsonDecoder[HNil] = _ => Right(HNil)

  implicit val cnilDecoder: ReprBsonDecoder[CNil] = _ => Left(ADTValueNotFound)

  implicit final def cpDecoder[K <: Symbol, V, T <: Coproduct : ReprBsonDecoder](implicit
                                                                                 w: Witness.Aux[K],
                                                                                 d: Lazy[BsonDecoder[V]]
                                                                                ): ReprBsonDecoder[FieldType[K, V] :+: T] = b => {
    val coProductTypeField = b.get(CoProductType)
    if (coProductTypeField != null &&
          coProductTypeField.isString &&
          coProductTypeField.asString().getValue == w.value.name) {
      d.value.apply(b).map(v => Inl(v.asInstanceOf[FieldType[K, V]]))
    } else {
      ReprBsonDecoder[T].apply(b).map(Inr(_))
    }
  }

}

trait ReprBsonDecoderInstances extends LowPriorityReprBsonDecoderInstances {

  implicit final def hlistNotNullDecoder[K <: Symbol, V, U, T <: HList](implicit
                                                                        w: Witness.Aux[K],
                                                                        uw: Strict[Unwrapped.Aux[V, U]],
                                                                        d: Lazy[BsonDecoderNotNull[U]],
                                                                        rt: Strict[ReprBsonDecoder[T]]
                                                                       ): ReprBsonDecoder[FieldType[K, V] :: T] = b => {
    val value = b.get(w.value.name)
    if (value == null) {
      Left(FieldNotFound(w.value.name))
    } else {
      d.value.apply(value)
        .map(v => uw.value.wrap(v)).asInstanceOf[Either[BsonError, FieldType[K, V]]]
        .flatMap(h => rt.value.apply(b).map(t => h :: t))
    }
  }

}

trait LowPriorityReprBsonDecoderInstances {

  implicit final def hlistDecoder[K <: Symbol, V, U, T <: HList](implicit
                                                                 w: Witness.Aux[K],
                                                                 uw: Strict[Unwrapped.Aux[V, U]],
                                                                 d: Lazy[BsonDecoder[U]],
                                                                 rt: Strict[ReprBsonDecoder[T]]
                                                                ): ReprBsonDecoder[FieldType[K, V] :: T] = b => {
    val value = b.get(w.value.name)
    d.value.apply(value)
      .map(v => uw.value.wrap(v)).asInstanceOf[Either[BsonError, FieldType[K, V]]]
      .flatMap(h => rt.value.apply(b).map(t => h :: t))
  }

}
