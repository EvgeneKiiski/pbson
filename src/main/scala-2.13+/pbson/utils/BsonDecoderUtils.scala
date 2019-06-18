package pbson.utils

import org.bson.{ BsonType, BsonValue }
import pbson.BsonError._
import pbson._
import pbson.utils.TraversableUtils._

import scala.util.Try

/**
  * @author Evgenii Kiiski
  */
trait BsonDecoderUtils {

  final def asStringDecoder[A](f: PartialFunction[String, A]): BsonDecoder[A] = b =>
    if (b.isString) {
      val str = b.asString().getValue
      f.andThen(Right.apply(_)).orElse[String, BsonDecoder.Result[A]] {
        case s => Left(UnexpectedValue(s))
      }.apply(str)
    } else {
      Left(UnexpectedType(b, BsonType.STRING))
    }

  final def array2MapDecoder[K, V](implicit d: BsonBiDecoder[K, V]): BsonDecoder[Map[K, V]] = {
    case null => Right(Map.empty)
    case b: BsonValue if b.isArray =>
      traverse2Map(b.asArray().getValues)(d.apply)
    case b => Left(UnexpectedType(b, BsonType.ARRAY))
  }

  final def enumDecoder[E <: Enumeration](enum: E): BsonDecoder[E#Value] = b =>
    BsonDecoder.stringDecoder(b).flatMap { str =>
      Try(enum.withName(str))
        .toEither.fold(e => Left(WrappedThrowable(e)), Right.apply)
    }

  final def eitherDecoder[A, B](leftKey: String, rightKey: String)(implicit
    da: BsonDecoder[A],
    db: BsonDecoder[B]
  ): BsonDecoder[Either[A, B]] = b =>
    if (b.getBsonType == BsonType.DOCUMENT) {
      val doc = b.asDocument()
      if (doc.containsKey(rightKey)) {
        db(doc.get(rightKey)).map(Right.apply)
      } else if (doc.containsKey(leftKey)) {
        da(doc.get(leftKey)).map(Left.apply)
      } else {
        Left(UnexpectedValue(b))
      }
    } else {
      Left(UnexpectedType(b, BsonType.DOCUMENT))
    }

  final def mapAsValueDecoder[K, V](f: V => K)(implicit d: BsonDecoder[V]): BsonDecoder[Map[K, V]] = {
    case null => Right(Map.empty)
    case b: BsonValue if b.isArray =>
      traverse2Seq(b.asArray().getValues)(d.apply).map(_.map(v => f(v) -> v).toMap)
    case b => Left(UnexpectedType(b, BsonType.ARRAY))
  }

}
