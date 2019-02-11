package pbson.utils

import org.mongodb.scala.bson.BsonValue
import pbson.{ BsonBiDecoder, BsonDecoder }
import pbson.BsonError.{ UnexpectedType, UnexpectedValue, WrappedThrowable }

import scala.collection.JavaConverters._
import org.bson.BsonType
import pbson.utils.TraversableUtils.traverse2Map

import scala.util.Try


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
      traverse2Map(b.asArray().getValues.asScala)(d.apply)
    case b => Left(UnexpectedType(b, BsonType.ARRAY))
  }

  final def enumDecoder[E <: Enumeration](enum: E): BsonDecoder[E#Value] = b =>
    BsonDecoder.stringDecoder(b).flatMap { str =>
      Try(enum.withName(str))
        .toEither.fold(e => Left(WrappedThrowable.apply(e)), Right.apply)
    }

}
