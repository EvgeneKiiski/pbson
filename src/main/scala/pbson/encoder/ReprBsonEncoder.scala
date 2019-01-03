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
  def apply(t: R): Map[String, BsonValue]
}

object ReprBsonEncoder {

  final def apply[R](implicit e: ReprBsonEncoder[R]): ReprBsonEncoder[R] = e

  implicit final def keyTagEncoder[K <: Symbol, V](implicit
                                                   ve: BsonEncoder[V],
                                                   w: Witness.Aux[K]
                                                  ): ReprBsonEncoder[FieldType[K, V]] =
    t => Map(w.value.name -> ve.apply(t.asInstanceOf[V]))


  implicit val hnilEncoder: ReprBsonEncoder[HNil] = _ => Map.empty

  implicit final def hlistEncoder[H: ReprBsonEncoder, T <: HList : ReprBsonEncoder]: ReprBsonEncoder[H :: T] = {
    l => ReprBsonEncoder[H].apply(l.head) ++ ReprBsonEncoder[T].apply(l.tail)
  }

  implicit val cnilEncoder: ReprBsonEncoder[CNil] = _ => Map.empty

  implicit final def coproductEncoder[H: ReprBsonEncoder, T <: Coproduct : ReprBsonEncoder]: ReprBsonEncoder[H :+: T] = {
    case Inl(head) => ReprBsonEncoder[H].apply(head)
    case Inr(tail) => ReprBsonEncoder[T].apply(tail)
    case _ => Map.empty
  }

}
