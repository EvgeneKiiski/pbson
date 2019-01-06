package pbson.encoder


import org.mongodb.scala.bson.BsonValue
import pbson.BsonEncoder
import shapeless._
import shapeless.labelled.FieldType

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

  implicit final def hlistEncoder[K <: Symbol, V, T <: HList : ReprBsonEncoder](implicit
                                                                                e: Lazy[BsonEncoder[V]],
                                                                                w: Witness.Aux[K]
                                                                               ): ReprBsonEncoder[FieldType[K, V] :: T] =
    l => (w.value.name, e.value.apply(l.head.asInstanceOf[V])) :: ReprBsonEncoder[T].apply(l.tail)

  implicit val cnilEncoder: ReprBsonEncoder[CNil] = _ => List.empty

  implicit final def coproductEncoder[K <: Symbol, V, T <: Coproduct : ReprBsonEncoder](implicit
                                                                                        e: Lazy[BsonEncoder[V]],
                                                                                        w: Witness.Aux[K]
                                                                                       ): ReprBsonEncoder[FieldType[K, V] :+: T] = {
    case Inl(head) => (w.value.name, e.value.apply(head.asInstanceOf[V])) :: Nil
    case Inr(tail) => ReprBsonEncoder[T].apply(tail)
  }

}
