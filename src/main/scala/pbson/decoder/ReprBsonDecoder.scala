package pbson.decoder

import org.bson.BsonDocument
import org.mongodb.scala.bson.BsonValue
import pbson.BsonDecoder.Result
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
  def apply(b: BsonDocument): ReprBsonDecoder.Result[R]
}

object ReprBsonDecoder {

  type Result[A] = Either[BsonError, A]

  @inline final def apply[R](implicit d: ReprBsonDecoder[R]): ReprBsonDecoder[R] = d

  private[this] def instance[R](f: BsonDocument => Result[R]): ReprBsonDecoder[R] = f(_)

  implicit final def hlistDecoder[K <: Symbol, V, U, T <: HList](implicit
                                                                 w: Witness.Aux[K],
                                                                 uw: Strict[Unwrapped.Aux[V, U]],
                                                                 d: Lazy[BsonDecoder[U]],
                                                                 rt: Strict[ReprBsonDecoder[T]]
                                                                ): ReprBsonDecoder[FieldType[K, V] :: T] = b => {
    val value = b.get(w.value.name)
    if (value == null) {
      Left(FieldNotFound(w.value.name))
    } else {
      d.value.apply(value).map(v => uw.value.wrap(v)).asInstanceOf[Either[BsonError, FieldType[K, V]]]
        .flatMap(h => rt.value.apply(b).map(t => h :: t))
    }
  }

  implicit val hnilDecoder: ReprBsonDecoder[HNil] = _ => Right(HNil)

  implicit val cnilDecoder: ReprBsonDecoder[CNil] = _ => Left(FieldNotFound("CNil"))

  implicit final def cpDecoder[K <: Symbol, V, T <: Coproduct : ReprBsonDecoder](implicit
                                                                                 w: Witness.Aux[K],
                                                                                 d: Lazy[BsonDecoder[V]]
                                                                                ): ReprBsonDecoder[FieldType[K, V] :+: T] =
    b => {
      println(s"codec: ${w.value.name} $b")
      if (b.containsKey(w.value.name)) {
        d.value.apply(b.get(w.value.name)).map(v => Inl(v.asInstanceOf[FieldType[K, V]]))
      } else {
        ReprBsonDecoder[T].apply(b).map(Inr(_))
      }
    }


}
