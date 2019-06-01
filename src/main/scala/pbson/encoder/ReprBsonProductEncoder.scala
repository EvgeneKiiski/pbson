package pbson.encoder

import org.bson.BsonDocument
import pbson.BsonEncoder
import shapeless.labelled.FieldType
import shapeless.{ ::, HList, HNil, Lazy, Strict, Witness }

/**
  * @author Eugene Kiyski
  */
abstract class ReprBsonProductEncoder[R] {

  def apply(doc: BsonDocument, r: R): BsonDocument

}

object ReprBsonProductEncoder extends ReprBsonProductEncoderInstances {

  @inline final def apply[A](implicit e: ReprBsonProductEncoder[A]): ReprBsonProductEncoder[A] = e

}

trait ReprBsonProductEncoderInstances extends LowPriorityReprBsonProductEncoderInstances {

  implicit final val hnilEncoder: ReprBsonProductEncoder[HNil] = (doc: BsonDocument, _: HNil) => doc

  implicit final def hlistConsEncoder[K <: Symbol, V, T <: HList](implicit
    w: Witness.Aux[K],
    e: Lazy[ReprBsonMaybeEncoder[V]],
    rt: Strict[ReprBsonProductEncoder[T]]
  ): ReprBsonProductEncoder[FieldType[K, V] :: T] = (doc: BsonDocument, l: FieldType[K, V] :: T) => {
    e.value.apply(doc, w.value.name, l.head)
    rt.value.apply(doc, l.tail)
  }
}

trait LowPriorityReprBsonProductEncoderInstances {

  implicit final def hlistEncoder[K <: Symbol, V, T <: HList](implicit
    w: Witness.Aux[K],
    e: Lazy[BsonEncoder[V]],
    rt: Strict[ReprBsonProductEncoder[T]]
  ): ReprBsonProductEncoder[FieldType[K, V] :: T] = (doc: BsonDocument, l: FieldType[K, V] :: T) => {
    doc.append(w.value.name, e.value.apply(l.head))
    rt.value.apply(doc, l.tail)
  }

}