package pbson.decoder

import org.bson.BsonDocument
import org.mongodb.scala.bson.BsonString
import pbson.BsonError.FieldNotFound
import pbson.BsonConst._
import pbson.{BsonDecoder, BsonError, BsonConst}
import shapeless._
import shapeless.labelled.FieldType


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
    val result = d.value.apply(value).map(v => uw.value.wrap(v)).asInstanceOf[Either[BsonError, FieldType[K, V]]]
      .flatMap(h => rt.value.apply(b).map(t => h :: t))
    if(result.isLeft && value == null) {
      Left(FieldNotFound(w.value.name))
    } else {
      result
    }
  }

  implicit val hnilDecoder: ReprBsonDecoder[HNil] = _ => Right(HNil)

  implicit val cnilDecoder: ReprBsonDecoder[CNil] = _ => Left(FieldNotFound("CNil"))

  implicit final def cpDecoder[K <: Symbol, V, T <: Coproduct : ReprBsonDecoder](implicit
                                                                                 w: Witness.Aux[K],
                                                                                 d: Lazy[BsonDecoder[V]]
                                                                                ): ReprBsonDecoder[FieldType[K, V] :+: T] = b =>
    if (b.get(CoProductType) == BsonString(w.value.name)) {
      d.value.apply(b).map(v => Inl(v.asInstanceOf[FieldType[K, V]]))
    } else {
      ReprBsonDecoder[T].apply(b).map(Inr(_))
    }

}
