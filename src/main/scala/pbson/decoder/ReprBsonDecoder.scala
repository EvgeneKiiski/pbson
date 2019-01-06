package pbson.decoder

import org.bson.BsonDocument
import pbson.BsonError.FieldNotFound
import pbson.{BsonDecoder, BsonError}
import pbson.encoder.ReprBsonEncoder
import shapeless._
import shapeless.labelled.FieldType
import shapeless.poly._
import shapeless.record._
import shapeless.ops.record._
import shapeless.ops.hlist.{Mapper, ToTraversable}
import shapeless.tag._


/**
  * @author Evgenii Kiiski 
  */
trait ReprBsonDecoder[R] {
  def apply(b: BsonDocument): Either[BsonError, R]
}

object ReprBsonDecoder {
  @inline final def apply[R](implicit d: ReprBsonDecoder[R]): ReprBsonDecoder[R] = d

  implicit final def hlistDecoder[K <: Symbol, V, T <: HList : ReprBsonDecoder](implicit
                                                                                d: Lazy[BsonDecoder[V]],
                                                                                w: Witness.Aux[K]
                                                                               ): ReprBsonDecoder[FieldType[K, V] :: T] = b => {
    val value = b.get(w.value.name)
    if (value == null) {
      Left(FieldNotFound(w.value.name))
    } else {
      d.value.apply(value).asInstanceOf[Either[BsonError, FieldType[K, V]]]
        .flatMap(h => ReprBsonDecoder[T].apply(b).map(t => h :: t))
    }
  }

  implicit val hnilDecoder: ReprBsonDecoder[HNil] = _ => Right(HNil)

  implicit val cnilDecoder: ReprBsonDecoder[CNil] = _ => Left(FieldNotFound("CNil"))

  implicit final def coproductDecoder[K <: Symbol, V, T <: Coproduct : ReprBsonDecoder](implicit
                                                                                        d: Lazy[BsonDecoder[V]],
                                                                                        w: Witness.Aux[K]
                                                                                       ): ReprBsonDecoder[FieldType[K, V] :+: T] = ???


}
