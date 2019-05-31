package pbson.encoder

import org.bson.{ BsonString, BsonValue }
import pbson.BsonConst.CoProductType
import pbson.BsonEncoder
import shapeless.labelled.FieldType
import shapeless.{ :+:, CNil, Coproduct, Inl, Inr, Lazy, Strict, Witness }

/**
  * @author Eugene Kiyski
  */
abstract class ReprBsonCoproductEncoder[R] {

  def apply(r: R): BsonValue

}

object ReprBsonCoproductEncoder {

  @inline final def apply[A](implicit e: ReprBsonCoproductEncoder[A]): ReprBsonCoproductEncoder[A] = e

  implicit val cnilEncoder: ReprBsonCoproductEncoder[CNil] =
    (r: CNil) => throw new RuntimeException("impossible state")

  implicit final def cpEncoder[K <: Symbol, V, T <: Coproduct](implicit
    w: Witness.Aux[K],
    e: Lazy[BsonEncoder[V]],
    t: Strict[ReprBsonCoproductEncoder[T]]
  ): ReprBsonCoproductEncoder[FieldType[K, V] :+: T] = {
    case Inl(head) =>
      val doc = e.value.apply(head.asInstanceOf[V]).asDocument()
      doc.append(CoProductType, new BsonString(w.value.name))
      doc
    case Inr(tail) => t.value.apply(tail)
  }

}