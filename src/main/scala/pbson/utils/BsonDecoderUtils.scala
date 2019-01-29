package pbson.utils

import org.mongodb.scala.bson.BsonValue
import pbson.{ BsonBiDecoder, BsonDecoder }
import pbson.BsonError.{ UnexpectedType, UnexpectedValue }

import scala.collection.JavaConverters._
import cats._
import cats.implicits._
import org.bson.BsonType


/**
  * @author Evgenii Kiiski 
  */
trait BsonDecoderUtils {

  final def asStringDecoder[A](f: PartialFunction[String, A]): BsonDecoder[A] = b =>
    if (b.isString) {
      val str = b.asString().getValue
      f.andThen(Right.apply).orElse[String, BsonDecoder.Result[A]] {
        case s => Left(UnexpectedValue(s))
      }.apply(str)
    } else {
      Left(UnexpectedType(b, BsonType.STRING))
    }

  final def array2MapDecoder[K, V](implicit d: BsonBiDecoder[K, V]): BsonDecoder[Map[K, V]] = {
    case null => Right(Map.empty)
    case b: BsonValue if b.isArray =>
      val seq: List[BsonValue] = b.asArray().getValues.asScala.toList
      seq.traverse(d.apply).map(_.toMap)
    case b => Left(UnexpectedType(b, BsonType.ARRAY))
  }

}