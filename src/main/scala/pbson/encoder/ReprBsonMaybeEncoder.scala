package pbson.encoder

import java.util

import org.bson.{ BsonArray, BsonDocument, BsonValue }
import pbson.{ BsonEncoder, BsonMapEncoder }
import shapeless.Lazy
import scala.collection.JavaConverters._

/**
  * @author Eugene Kiyski
  */
abstract class ReprBsonMaybeEncoder[R] {

  def apply(doc: BsonDocument, name: String, value: R): BsonDocument

}

object ReprBsonMaybeEncoder {

  @inline final def apply[A](implicit e: ReprBsonMaybeEncoder[A]): ReprBsonMaybeEncoder[A] = e

  implicit final def optionEncoder[A](implicit
    e: Lazy[BsonEncoder[A]]
  ): ReprBsonMaybeEncoder[Option[A]] = new ReprBsonMaybeEncoder[Option[A]] {
    override def apply(doc: BsonDocument, name: String, value: Option[A]): BsonDocument = value match {
      case Some(v) => doc.append(name, e.value.apply(v))
      case None => doc
    }
  }

  implicit final def seqEncoder[A](
    implicit e: BsonEncoder[A]
  ): ReprBsonMaybeEncoder[Seq[A]] = (doc: BsonDocument, name: String, t: Seq[A]) => {
    if (t.nonEmpty) {
      doc.append(name, new BsonArray(t.map(e.apply).asJava))
    } else {
      doc
    }
  }

  implicit final def listEncoder[A](implicit
    e: BsonEncoder[A]
  ): ReprBsonMaybeEncoder[List[A]] = (doc: BsonDocument, name: String, t: List[A]) =>
    if (t.isEmpty) {
      doc
    } else {
      doc.append(name, new BsonArray(t.map(e.apply).asJava))
    }

  implicit final def setEncoder[A](implicit
    e: BsonEncoder[A]
  ): ReprBsonMaybeEncoder[Set[A]] = (doc: BsonDocument, name: String, t: Set[A]) =>
    if (t.isEmpty) {
      doc
    } else {
      val set: util.Set[BsonValue] = t.map(e.apply).asJava
      val list = new util.ArrayList[BsonValue](set)
      doc.append(name, new BsonArray(list))
    }

  implicit final def vectorEncoder[A](implicit
    e: BsonEncoder[A]
  ): ReprBsonMaybeEncoder[Vector[A]] = (doc: BsonDocument, name: String, t: Vector[A]) =>
    if (t.isEmpty) {
      doc
    } else {
      doc.append(name, new BsonArray(t.map(e.apply).asJava))
    }

  implicit final def mapEncoderDocument[K, V](implicit
    e: BsonMapEncoder[K, V]
  ): ReprBsonMaybeEncoder[Map[K, V]] = (doc: BsonDocument, name: String, t: Map[K, V]) =>
    if (t.isEmpty) {
      doc
    } else {
      val d = new BsonDocument()
      d.putAll(t.map(e.apply).asJava)
      doc.append(name, d)
    }

}
