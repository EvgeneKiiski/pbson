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
  def apply(d: BsonDocument): Either[BsonError, R]
}

object ReprBsonDecoder {
  final def apply[R](implicit d: ReprBsonDecoder[R]): ReprBsonDecoder[R] = d

  implicit final def keyTagDecoder[K <: Symbol, V](implicit
                                                   vd: BsonDecoder[V],
                                                   w: Witness.Aux[K]
                                                  ): ReprBsonDecoder[FieldType[K, V]] =
    d => {
      val value = d.get(w.value.name)
      if (value == null) {
        Left(FieldNotFound(w.value.name))
      } else {
        vd.apply(value).asInstanceOf[Either[BsonError, FieldType[K, V]]]
      }
    }

  implicit final def hlistDecoder[H: ReprBsonDecoder, T <: HList : ReprBsonDecoder]: ReprBsonDecoder[H :: T] = {
    d =>
      for {
        h <- ReprBsonDecoder[H].apply(d)
        t <- ReprBsonDecoder[T].apply(d)
      } yield h :: t
  }

  implicit val hnilDecoder: ReprBsonDecoder[HNil] = _ => Right(HNil)

  implicit val cnilDecoder: ReprBsonDecoder[CNil] = _ => Left(FieldNotFound("CNil"))

  //  implicit final def coproductDecoder[H: ReprBsonDecoder, T <: Coproduct : ReprBsonDecoder]: ReprBsonDecoder[H :+: T] = {
  //    case Inl(head) => ReprBsonDecoder[H].apply(head)
  //    case Inr(tail) => ReprBsonDecoder[T].apply(tail)
  //  }


}
