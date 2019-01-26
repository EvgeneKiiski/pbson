package pbson.utils

import org.mongodb.scala.bson.BsonValue
import pbson.{BsonBiDecoder, BsonDecoder}
import pbson.BsonError.InvalidType
import scala.collection.JavaConverters._
import cats._
import cats.implicits._


/**
  * @author Evgenii Kiiski 
  */
trait BsonDecoderUtils {

  final def asStringDecoder[A](f: PartialFunction[String, A]): BsonDecoder[A] = b =>
    if (b.isString) {
      val str = b.asString().getValue
      f.andThen(Right.apply).orElse[String, BsonDecoder.Result[A]] {
        case s => Left(InvalidType(s))
      }.apply(str)
    } else {
      Left(InvalidType(b.toString))
    }

  final def array2MapDecoder[K, V](implicit d: BsonBiDecoder[K, V]): BsonDecoder[Map[K, V]] = {
    case null => Right(Map.empty)
    case b: BsonValue if b.isArray =>
      val seq: List[BsonValue] = b.asArray().getValues.asScala.toList
      seq.traverse(d.apply).map(_.toMap)
    case b => Left(InvalidType(b))
  }

}
