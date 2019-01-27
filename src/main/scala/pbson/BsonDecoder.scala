package pbson

import cats.implicits._
import org.bson.BsonType
import org.mongodb.scala.bson.{BsonArray, BsonNull, BsonString, BsonValue}
import pbson.BsonError.{FieldNotFound, InvalidType}

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

  @inline private[this] def instance[A](f: BsonValue => Result[A]): BsonDecoder[A] = b =>
    if (b != null) {
      f(b)
    } else {
      Left(FieldNotFound("null"))
    }


  implicit final val unitDecoder: BsonDecoder[Unit] = {
    case null => Left(FieldNotFound("null"))
    case b if b.getBsonType == BsonType.NULL => Right(())
    case b => Left(BsonError.InvalidType(s"${b.getBsonType} expected: BsonNull"))
  }


  implicit final val stringDecoder: BsonDecoder[String] = {
    case null => Left(FieldNotFound("null"))
    case b if b.getBsonType == BsonType.STRING => Right(b.asInstanceOf[BsonString].getValue)
    case b => Left(BsonError.InvalidType(s"${b.getBsonType} expected: String"))
  }

  implicit final val charDecoder: BsonDecoder[Char] = {
    case null => Left(FieldNotFound("null"))
    case b if b.getBsonType == BsonType.STRING =>
      val str = b.asInstanceOf[BsonString].getValue
      if(str.size == 1)
        Right(str.head)
      else
        Left(BsonError.InvalidType(s"${b.getBsonType} expected: BsonString lenght 1"))
    case b => Left(BsonError.InvalidType(s"${b.getBsonType} expected: BsonString "))
  }

  implicit final val shortDecoder: BsonDecoder[Short] = instance {
    b => {
      if (b.isInt32) {
        Right(b.asInt32().intValue().toShort)
      } else {
        Left(BsonError.InvalidType(s" ${b.getBsonType} expected: Int32"))
      }
    }
  }

  implicit final val intDecoder: BsonDecoder[Int] = instance {
    b => {
      if (b.isInt32) {
        Right(b.asInt32().getValue)
      } else {
        Left(BsonError.InvalidType(s" ${b.getBsonType} expected: Int"))
      }
    }
  }


  implicit final val longDecoder: BsonDecoder[Long] = instance {
    b => {
      if (b.isInt64) {
        Right(b.asInt64().getValue)
      } else {
        Left(BsonError.InvalidType(s" ${b.getBsonType} expected: Int"))
      }
    }
  }

  implicit final val doubleDecoder: BsonDecoder[Double] = instance {
    b =>
      Either.cond(
        b.isDouble,
        b.asDouble().getValue,
        BsonError.InvalidType(s"${b.getBsonType} expected: Double")
      )
  }

  implicit final val floatDecoder: BsonDecoder[Float] = instance {
    b =>
      Either.cond(
        b.isDouble,
        b.asDouble().getValue.toFloat,
        BsonError.InvalidType(s"${b.getBsonType} expected: Double")
      )
  }

  implicit final val booleanDecoder: BsonDecoder[Boolean] = instance {
    b =>
      Either.cond(
        b.isBoolean,
        b.asBoolean().getValue,
        BsonError.InvalidType(s"${b.getBsonType} expected: Boolean")
      )
  }

  implicit final def optionDecoder[A](implicit d: BsonDecoder[A]): BsonDecoder[Option[A]] = {
    case null => Right(None)
    case b => d(b).map(Some.apply)
  }

  implicit final def seqDecoder[A](implicit d: BsonDecoder[A]): BsonDecoder[Seq[A]] = {
    case null => Right(Seq.empty)
    case b: BsonValue if b.isArray =>
      val seq: List[BsonValue] = b.asArray().getValues.asScala.toList
      seq.traverse(d.apply)
    case b => Left(InvalidType(b.toString))
  }

  implicit final def mapDecoder[K, V](implicit d: BsonMapDecoder[K, V]): BsonDecoder[Map[K, V]] = {
    case null => Right(Map.empty)
    case b: BsonValue if b.isDocument =>
      b.asDocument().asScala.map(d.apply).toList.sequence.map(_.toMap)
    case b => Left(InvalidType(b.toString))
  }

}


