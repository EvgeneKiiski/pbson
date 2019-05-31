package pbson

import java.util.UUID

import org.bson._
import org.bson.types.Decimal128
import pbson.BsonDecoder.Result
import pbson.BsonError._
import pbson.decoder.DerivedBsonDecoder
import shapeless.Lazy
import pbson.utils.TraversableUtils._

import scala.util.control.NonFatal

/**
  * @author Evgenii Kiiski 
  */
abstract class BsonDecoder[A] { self =>
  def apply(b: BsonValue): BsonDecoder.Result[A]

  final def map[B](f: A => B): BsonDecoder[B] = new BsonDecoder[B] {
    override def apply(b: BsonValue): Result[B] = self(b).map(f)
  }

  final def flatMap[B](f: A => BsonDecoder[B]): BsonDecoder[B] = new BsonDecoder[B] {
    override def apply(b: BsonValue): Result[B] = self(b) match {
      case Right(a) => f(a)(b)
      case l @ Left(_) => l.asInstanceOf[Result[B]]
    }
  }

  final def handleErrorWith(f: BsonError => BsonDecoder[A]): BsonDecoder[A] = new BsonDecoder[A] {
    override def apply(b: BsonValue): Result[A] = self(b) match {
      case r @ Right(_) => r
      case Left(e) => f(e)(b)
    }
  }

  final def product[B](fb: BsonDecoder[B]): BsonDecoder[(A, B)] = new BsonDecoder[(A, B)] {
    override def apply(b: BsonValue): Result[(A, B)] = self(b) match {
      case Right(a) => fb(b) match {
        case Right(b) => Right((a, b))
        case l @ Left(_) => l.asInstanceOf[Result[(A, B)]]
      }
      case l @ Left(_) => l.asInstanceOf[Result[(A, B)]]
    }
  }

  final def or[AA >: A](d: => BsonDecoder[AA]): BsonDecoder[AA] = new BsonDecoder[AA] {
    override def apply(b: BsonValue): Result[AA] = self(b) match {
      case r @ Right(_) => r
      case Left(_) => d(b)
    }
  }
}

object BsonDecoder extends BsonDecoderInstances {

  type Result[A] = Either[BsonError, A]

  @inline final def apply[A](implicit d: BsonDecoder[A]): BsonDecoder[A] = d

  final def pure[A](x: A): BsonDecoder[A] = _ => Right(x)

  final def raiseError[A](e: BsonError): BsonDecoder[A] = _ => Left(e)

  final def fromEither[A](a: Result[A]): BsonDecoder[A] = _ => a

  private[pbson] val RIGHT_NULL = Right(null)
}


trait BsonDecoderInstances extends LowPriorityBsonDecoderInstances {

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
    } else if (b.getBsonType == BsonType.NULL) {
      BsonDecoder.RIGHT_NULL
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
        Left(UnexpectedEmptyString)
    } else {
      Left(UnexpectedType(b, BsonType.STRING))
    }
  }

  implicit final val javaCharDecoder: BsonDecoderNotNull[java.lang.Character] = b =>
    if (b.getBsonType == BsonType.NULL) {
      BsonDecoder.RIGHT_NULL
    } else {
      charDecoder.map(java.lang.Character.valueOf)(b)
    }


  implicit final val shortDecoder: BsonDecoderNotNull[Short] = { b =>
    if (b.getBsonType == BsonType.INT32) {
      Right(b.asInstanceOf[BsonInt32].intValue().toShort)
    } else {
      Left(UnexpectedType(b, BsonType.INT32))
    }
  }

  implicit final val javaShortDecoder: BsonDecoderNotNull[java.lang.Short] = b =>
    if (b.getBsonType == BsonType.NULL) {
      BsonDecoder.RIGHT_NULL
    } else {
      shortDecoder.map(java.lang.Short.valueOf)(b)
    }

  implicit final val intDecoder: BsonDecoderNotNull[Int] = { b =>
    if (b.getBsonType == BsonType.INT32) {
      Right(b.asInstanceOf[BsonInt32].getValue)
    } else {
      Left(UnexpectedType(b, BsonType.INT32))
    }
  }

  implicit final val javaIntDecoder: BsonDecoderNotNull[java.lang.Integer] = b =>
    if (b.getBsonType == BsonType.NULL) {
      BsonDecoder.RIGHT_NULL
    } else {
      intDecoder.map(java.lang.Integer.valueOf)(b)
    }

  implicit final val longDecoder: BsonDecoderNotNull[Long] = { b =>
    if (b.getBsonType == BsonType.INT64) {
      Right(b.asInstanceOf[BsonInt64].getValue)
    } else {
      Left(UnexpectedType(b, BsonType.INT64))
    }
  }

  implicit final val javaLongDecoder: BsonDecoderNotNull[java.lang.Long] = b =>
    if (b.getBsonType == BsonType.NULL) {
      BsonDecoder.RIGHT_NULL
    } else {
      longDecoder.map(java.lang.Long.valueOf)(b)
    }

  implicit final val doubleDecoder: BsonDecoderNotNull[Double] = { b =>
    if (b.getBsonType == BsonType.DOUBLE) {
      Right(b.asInstanceOf[BsonDouble].getValue)
    } else {
      Left(UnexpectedType(b, BsonType.DOUBLE))
    }
  }

  implicit final val javaDoubleDecoder: BsonDecoderNotNull[java.lang.Double] = b =>
    if (b.getBsonType == BsonType.NULL) {
      BsonDecoder.RIGHT_NULL
    } else {
      doubleDecoder.map(java.lang.Double.valueOf)(b)
    }

  implicit final val floatDecoder: BsonDecoderNotNull[Float] = { b =>
    if (b.getBsonType == BsonType.DOUBLE) {
      Right(b.asInstanceOf[BsonDouble].getValue.toFloat)
    } else {
      Left(UnexpectedType(b, BsonType.DOUBLE))
    }
  }

  implicit final val javaFloatDecoder: BsonDecoderNotNull[java.lang.Float] = b =>
    if (b.getBsonType == BsonType.NULL) {
      BsonDecoder.RIGHT_NULL
    } else {
      floatDecoder.map(java.lang.Float.valueOf)(b)
    }

  implicit final val booleanDecoder: BsonDecoderNotNull[Boolean] = { b =>
    if (b.getBsonType == BsonType.BOOLEAN) {
      Right(b.asInstanceOf[BsonBoolean].getValue)
    } else {
      Left(UnexpectedType(b, BsonType.BOOLEAN))
    }
  }

  implicit final val javaBooleanDecoder: BsonDecoderNotNull[java.lang.Boolean] = b =>
    if (b.getBsonType == BsonType.NULL) {
      BsonDecoder.RIGHT_NULL
    } else {
      booleanDecoder.map(java.lang.Boolean.valueOf)(b)
    }

  implicit final val uuidDecoder: BsonDecoderNotNull[UUID] = { b =>
    if (b.getBsonType == BsonType.NULL) {
      BsonDecoder.RIGHT_NULL
    } else if (b.getBsonType == BsonType.BINARY) {
      try {
        Right(b.asInstanceOf[BsonBinary].asUuid())
      } catch {
        case NonFatal(e) => Left(WrappedThrowable(e))
      }
    } else {
      Left(UnexpectedType(b, BsonType.STRING))
    }
  }

  implicit final val decimal128Decoder: BsonDecoderNotNull[Decimal128] = { b =>
    if (b.getBsonType == BsonType.DECIMAL128) {
      Right(b.asInstanceOf[BsonDecimal128].getValue)
    } else if (b.getBsonType == BsonType.NULL) {
      BsonDecoder.RIGHT_NULL
    } else {
      Left(UnexpectedType(b, BsonType.DECIMAL128))
    }
  }

  implicit final val javaDateDecoder: BsonDecoderNotNull[java.util.Date] = b =>
    if (b.getBsonType == BsonType.DATE_TIME) {
      Right(new java.util.Date(b.asInstanceOf[BsonDateTime].getValue))
    } else if (b.getBsonType == BsonType.NULL) {
      BsonDecoder.RIGHT_NULL
    } else {
      Left(UnexpectedType(b, BsonType.DATE_TIME))
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
      traverse2Seq(b.asInstanceOf[BsonArray].getValues)(d.apply)
    } else {
      Left(UnexpectedType(b, BsonType.ARRAY))
    }
  }

  implicit final def listDecoder[A](implicit d: BsonDecoder[A]): BsonDecoder[List[A]] = { b =>
    if (b == null) {
      Right(List.empty)
    } else if (b.getBsonType == BsonType.ARRAY) {
      traverse2List(b.asInstanceOf[BsonArray].getValues)(d.apply)
    } else {
      Left(UnexpectedType(b, BsonType.ARRAY))
    }
  }

  implicit final def setDecoder[A](implicit d: BsonDecoder[A]): BsonDecoder[Set[A]] = { b =>
    if (b == null) {
      Right(Set.empty)
    } else if (b.getBsonType == BsonType.ARRAY) {
      traverse2Set(b.asInstanceOf[BsonArray].getValues)(d.apply)
    } else {
      Left(UnexpectedType(b, BsonType.ARRAY))
    }
  }

  implicit final def vectorDecoder[A](implicit d: BsonDecoder[A]): BsonDecoder[Vector[A]] = { b =>
    if (b == null) {
      Right(Vector.empty)
    } else if (b.getBsonType == BsonType.ARRAY) {
      traverse2Vector(b.asInstanceOf[BsonArray].getValues)(d.apply)
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
      traverse2Map(b.asInstanceOf[BsonDocument])(d.apply)
    } else {
      Left(UnexpectedType(b, BsonType.DOCUMENT))
    }
  }

}

trait LowPriorityBsonDecoderInstances {

  implicit final def deriveDecoder[A](implicit
    decode: Lazy[DerivedBsonDecoder[A]]
  ): BsonDecoder[A] = decode.value

}


