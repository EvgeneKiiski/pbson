package pbson.encoder


import org.mongodb.scala.bson.{BsonString, BsonValue}
import pbson.BsonConst._
import pbson.{BsonEncoder, BsonConst}
import shapeless._
import shapeless.labelled.FieldType

import scala.collection.JavaConverters._
import scala.language.experimental.macros

/**
  * @author Evgenii Kiiski
  */
abstract class ReprBsonEncoder[R] {
  def apply(t: R): List[(String, BsonValue)]
}

object ReprBsonEncoder {

  @inline final def apply[R](implicit e: ReprBsonEncoder[R]): ReprBsonEncoder[R] = e

  implicit val hnilEncoder: ReprBsonEncoder[HNil] = _ => List.empty

  implicit final def hlistEncoder[K <: Symbol, V, U, T <: HList](implicit
                                                                 w: Witness.Aux[K],
                                                                 uw: Strict[Unwrapped.Aux[V, U]],
                                                                 e: Lazy[BsonEncoder[U]],
                                                                 rt: Strict[ReprBsonEncoder[T]]
                                                                ): ReprBsonEncoder[FieldType[K, V] :: T] =
    l => (w.value.name, e.value.apply(uw.value.unwrap(l.head))) :: rt.value.apply(l.tail)

  implicit val cnilEncoder: ReprBsonEncoder[CNil] = _ => List.empty

  implicit final def cpEncoder[K <: Symbol, V, T <: Coproduct : ReprBsonEncoder](implicit
                                                                                 w: Witness.Aux[K],
                                                                                 e: Lazy[BsonEncoder[V]],
                                                                                ): ReprBsonEncoder[FieldType[K, V] :+: T] = {
    case Inl(head) =>
      e.value.apply(head.asInstanceOf[V])
        .asDocument()
        .append(CoProductType, BsonString(w.value.name))
        .entrySet()
        .asScala
        .map(e => (e.getKey, e.getValue))
        .toList
    case Inr(tail) => ReprBsonEncoder[T].apply(tail)
  }

}
