package pbson

import org.bson.{ BsonType, BsonValue }
import pbson.BsonError.{ ArrayValueNotFound, FieldNotFound, UnexpectedType }

import scala.collection.JavaConverters._
import BsonDecoder.Result

/**
  * @author Evgenii Kiiski
  */
final case class BCursor(private[BCursor] val value: Result[BsonValue]) extends AnyVal {

  def down(key: String): BCursor = BCursor {
    value.flatMap { v =>
      if (v.getBsonType == BsonType.DOCUMENT) {
        val doc = v.asDocument()
        if (doc.containsKey(key)) {
          Right(doc.get(key))
        } else {
          Left(FieldNotFound(key))
        }
      } else {
        Left(UnexpectedType(v, BsonType.DOCUMENT))
      }
    }
  }

  def find(f: BsonValue => Boolean): BCursor = BCursor {
    value.flatMap { v =>
      if (v.getBsonType == BsonType.ARRAY) {
        v.asArray()
          .getValues
          .asScala
          .find(f)
          .fold[Result[BsonValue]](Left(ArrayValueNotFound()))(Right.apply)
      } else {
        Left(UnexpectedType(v, BsonType.ARRAY))
      }
    }
  }

  def as[A](implicit d: BsonDecoder[A]): Result[A] = value.flatMap(d.apply)

  def get[A](k: String)(implicit d: BsonDecoder[A]): Result[A] = down(k).as[A]

  def getOrElse[A](k: String)(fallback: => A)(implicit d: BsonDecoder[A]): Result[A] =
    get[A](k) match {
      case Right(a) => Right(a)
      case Left(FieldNotFound(_)) => Right(fallback)
      case l @ Left(_) => l.asInstanceOf[Result[A]]
    }
}

