package pbson.decoder

import org.bson.BsonDocument
import pbson.BsonError.{ ADTValueNotFound, FieldNotFound }
import pbson.BsonConst._
import pbson.BsonDecoder.{ BsonDecoderNotNull, Result }
import pbson.{ BsonDecoder, BsonError }
import shapeless._
import shapeless.labelled.FieldType


/**
  * @author Evgenii Kiiski 
  */
abstract class ReprBsonDecoder[R] {
  def apply(b: BsonDocument): Result[R]
}

object ReprBsonDecoder extends ReprBsonDecoderInstances {

  @inline final def apply[R](implicit d: ReprBsonDecoder[R]): ReprBsonDecoder[R] = d

  implicit final val hnilDecoder: ReprBsonDecoder[HNil] = _ => Right(HNil)

  implicit final val cnilDecoder: ReprBsonDecoder[CNil] = _ => Left(ADTValueNotFound)

  implicit final def cpDecoder[K <: Symbol, V, T <: Coproduct](implicit
    w: Witness.Aux[K],
    d: Lazy[BsonDecoder[V]],
    td: Strict[ReprBsonDecoder[T]]
  ): ReprBsonDecoder[FieldType[K, V] :+: T] = b => {
    val coProductTypeField = b.get(CoProductType)
    if (coProductTypeField != null &&
      coProductTypeField.isString &&
      coProductTypeField.asString().getValue == w.value.name) {
      d.value.apply(b).map(v => Inl(v.asInstanceOf[FieldType[K, V]]))
    } else {
      td.value.apply(b).map(Inr(_))
    }
  }

}

trait ReprBsonDecoderInstances extends LowPriorityReprBsonDecoderInstances {

  implicit final def hlistNotNullDecoder[K <: Symbol, V, T <: HList](implicit
    w: Witness.Aux[K],
    d: Lazy[BsonDecoderNotNull[V]],
    rt: Strict[ReprBsonDecoder[T]]
  ): ReprBsonDecoder[FieldType[K, V] :: T] = b => {
    val value = b.get(w.value.name)
    if (value == null) {
      Left(FieldNotFound(w.value.name))
    } else {
      d.value.apply(value).asInstanceOf[Either[BsonError, FieldType[K, V]]]
        .flatMap(h => rt.value.apply(b).map(t => h :: t))
    }
  }

}

trait LowPriorityReprBsonDecoderInstances {

  implicit final def hlistDecoder[K <: Symbol, V, T <: HList](implicit
    w: Witness.Aux[K],
    d: Lazy[BsonDecoder[V]],
    rt: Strict[ReprBsonDecoder[T]]
  ): ReprBsonDecoder[FieldType[K, V] :: T] = b => {
    val value = b.get(w.value.name)
    d.value.apply(value).asInstanceOf[Either[BsonError, FieldType[K, V]]]
      .flatMap(h => rt.value.apply(b).map(t => h :: t))
  }

}
