package pbson

import org.bson.{BsonArray, BsonType, BsonUndefined, BsonValue}
import pbson.BCursor.{BCursorArray, BCursorError, BCursorValue}
import pbson.BsonError.{ArrayValueNotFound, FieldNotFound, UnexpectedType}

import scala.collection.JavaConverters._

/**
  * @author Evgenii Kiiski
  */
abstract sealed class BCursor() {
  def value: BsonValue

  def downField(key: String): BCursor = {
    if (value.getBsonType == BsonType.DOCUMENT) {
      val doc = value.asDocument()
      if (doc.containsKey(key)) {
        BCursorValue(doc.get(key))
      } else {
        BCursorError(FieldNotFound(key))
      }
    } else {
      BCursorError(UnexpectedType(value, BsonType.DOCUMENT))
    }
  }

  def downArray(key: String): BCursor = {
    if (value.getBsonType == BsonType.ARRAY) {
      BCursorArray(value.asArray())
    } else {
      BCursorError(UnexpectedType(value, BsonType.DOCUMENT))
    }
  }

  final def as[A](implicit d: BsonDecoder[A]): BsonDecoder.Result[A] = d.apply(value)

  final def get[A](k: String)(implicit d: BsonDecoder[A]): BsonDecoder.Result[A] = downField(k).as[A]

  final def getOrElse[A](k: String)(fallback: => A)(implicit d: BsonDecoder[A]): BsonDecoder.Result[A] =
    get[Option[A]](k) match {
      case Right(Some(a)) => Right(a)
      case Right(None) => Right(fallback)
      case l@Left(_) => l.asInstanceOf[BsonDecoder.Result[A]]
    }
}

object BCursor {

  final case class BCursorValue(value: BsonValue) extends BCursor {

  }

  final case class BCursorError(error: BsonError) extends BCursor {
    override def downField(key: String): BCursor = this

    override def value: BsonValue = new BsonUndefined()
  }

  final case class BCursorArray(value: BsonArray) extends BCursor {
    def find(f: BsonValue => Boolean): BCursor = {
      value
        .getValues
        .asScala
        .find(f)
        .fold[BCursor](BCursorError(ArrayValueNotFound()))(BCursorValue.apply)
    }
  }

}
