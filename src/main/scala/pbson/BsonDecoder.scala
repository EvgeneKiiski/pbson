package pbson

import cats.implicits._
import org.bson.BsonType
import org.mongodb.scala.bson.{ BsonArray, BsonBoolean, BsonDocument, BsonDouble, BsonInt32, BsonInt64, BsonNull, BsonString, BsonValue }
import pbson.BsonError._

import scala.collection.JavaConverters._

/**
  * @author Evgenii Kiiski 
  */
abstract class BsonDecoder[A] {
  def apply(b: BsonValue): BsonDecoder.Result[A]
}

object BsonDecoder {

  type Result[A] = Either[BsonError, A]

  @inline final def apply[A](implicit d: BsonDecoder[A]): BsonDecoder[A] = d

  abstract class BsonDecoderNotNull[A] extends BsonDecoder[A]

  implicit final val unitDecoder: BsonDecoderNotNull[Unit] = { b =>
    if (b.getBsonType == BsonType.NULL) {
      Right(())
    } else {
      Left(UnexpectedType(b, BsonType.NULL))
    }
  }


  implicit final val stringDecoder: BsonDecoderNotNull[String] = { b =>
    if (b.getBsonType == BsonType.STRING) {
      Right(b.asInstanceOf[BsonString].getValue)
    } else {
      Left(UnexpectedType(b, BsonType.STRING))
    }
  }

  implicit final val charDecoder: BsonDecoderNotNull[Char] = { b =>
    if (b.getBsonType == BsonType.STRING) {
      val str = b.asInstanceOf[BsonString].getValue
      if (str.nonEmpty)
        Right(str.head)
      else
        Left(UnexpectedValue(s"${b.getBsonType} expected: BsonString lenght 1"))
    } else {
      Left(UnexpectedType(b, BsonType.STRING))
    }
  }

  implicit final val shortDecoder: BsonDecoderNotNull[Short] = { b =>
    if (b.getBsonType == BsonType.INT32) {
      Right(b.asInstanceOf[BsonInt32].intValue().toShort)
    } else {
      Left(UnexpectedType(b, BsonType.INT32))
    }
  }


  implicit final val intDecoder: BsonDecoderNotNull[Int] = { b =>
    if (b.getBsonType == BsonType.INT32) {
      Right(b.asInstanceOf[BsonInt32].getValue)
    } else {
      Left(UnexpectedType(b, BsonType.INT32))
    }
  }


  implicit final val longDecoder: BsonDecoderNotNull[Long] = { b =>
    if (b.getBsonType == BsonType.INT64) {
      Right(b.asInstanceOf[BsonInt64].getValue)
    } else {
      Left(UnexpectedType(b, BsonType.INT64))
    }
  }

  implicit final val doubleDecoder: BsonDecoderNotNull[Double] = { b =>
    if (b.getBsonType == BsonType.DOUBLE) {
      Right(b.asInstanceOf[BsonDouble].getValue)
    } else {
      Left(UnexpectedType(b, BsonType.DOUBLE))
    }
  }

  implicit final val floatDecoder: BsonDecoderNotNull[Float] = { b =>
    if (b.getBsonType == BsonType.DOUBLE) {
      Right(b.asInstanceOf[BsonDouble].getValue.toFloat)
    } else {
      Left(UnexpectedType(b, BsonType.DOUBLE))
    }
  }

  implicit final val booleanDecoder: BsonDecoderNotNull[Boolean] = { b =>
    if (b.getBsonType == BsonType.BOOLEAN) {
      Right(b.asInstanceOf[BsonBoolean].getValue)
    } else {
      Left(UnexpectedType(b, BsonType.BOOLEAN))
    }
  }

  implicit final def optionDecoder[A](implicit d: BsonDecoder[A]): BsonDecoder[Option[A]] = { b =>
    if (b == null) {
      Right(None)
    } else {
      d(b).map(Some.apply)
    }
  }

  implicit final def seqDecoder[A](implicit d: BsonDecoder[A]): BsonDecoder[Seq[A]] = { b =>
    if (b == null) {
      Right(Seq.empty)
    } else if (b.getBsonType == BsonType.ARRAY) {
      b.asInstanceOf[BsonArray].getValues.asScala.toList.traverse(d.apply)
    } else {
      Left(UnexpectedType(b, BsonType.ARRAY))
    }
  }

  implicit final def mapDecoder[K, V](implicit
    d: BsonMapDecoder[K, V]
  ): BsonDecoder[Map[K, V]] = { b =>
    if (b == null) {
      Right(Map.empty)
    } else if (b.getBsonType == BsonType.DOCUMENT) {
      b.asInstanceOf[BsonDocument].asScala.map(d.apply).toList.sequence.map(_.toMap)
    } else {
      Left(UnexpectedType(b, BsonType.DOCUMENT))
    }
  }

}


