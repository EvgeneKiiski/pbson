package pbson.encoder


import org.mongodb.scala.bson.BsonValue
import pbson.{BsonADTEncoder, BsonEncoder}
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

  implicit final def hlistEncoder[K <: Symbol, V, U, T <: HList](implicit
                                                                 w: Witness.Aux[K],
                                                                 uw: Strict[Unwrapped.Aux[V, U]],
                                                                 e: Lazy[BsonEncoder[U]],
                                                                 rt: Strict[ReprBsonEncoder[T]]
                                                                ): ReprBsonEncoder[FieldType[K, V] :: T] =
    l => (w.value.name, e.value.apply(uw.value.unwrap(l.head))) :: rt.value.apply(l.tail)

  implicit val cnilEncoder: ReprBsonEncoder[CNil] = _ => List.empty

  implicit final def adtEncoder[V]: BsonADTEncoder[V] = new BsonADTEncoder[V] {
    override def apply(name: String, t: BsonValue): List[(String, BsonValue)] = (name, t) :: Nil
  }

  implicit final def cpEncoder[K <: Symbol, V, T <: Coproduct : ReprBsonEncoder](implicit
                                                                                 e: Lazy[BsonEncoder[V]],
                                                                                 //h: Lazy[BsonADTEncoder[V]],
                                                                                 w: Witness.Aux[K]
                                                                                ): ReprBsonEncoder[FieldType[K, V] :+: T] = {
    case Inl(head) => {
      println(s"key: ${w.value.name} -> $head")
      //h.value(w.value.name, e.value.apply(head.asInstanceOf[V]))
      (w.value.name, e.value.apply(head.asInstanceOf[V])) :: Nil
    }
    case Inr(tail) => ReprBsonEncoder[T].apply(tail)
  }

}
