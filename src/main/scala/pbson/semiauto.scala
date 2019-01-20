package pbson

import cats.implicits._
import org.mongodb.scala.bson.{BsonArray, BsonNull, BsonString, BsonValue}
import pbson.BsonError.InvalidType
import pbson.decoder.DerivedBsonDecoder
import pbson.encoder.DerivedBsonEncoder
import shapeless._

import scala.collection.JavaConverters._

/**
  * @author Evgenii Kiiski 
  */
object semiauto {

  final def deriveEncoder[A](implicit encode: Lazy[DerivedBsonEncoder[A]]): BsonEncoder[A] = encode.value

  final def deriveDecoder[A](implicit decode: Lazy[DerivedBsonDecoder[A]]): BsonDecoder[A] = decode.value

  final def validateDeriveDecoder[A](validator: A => BsonDecoder.Result[A])(implicit
                                                                            decode: Lazy[DerivedBsonDecoder[A]]
  ): BsonDecoder[A] = bsonValidate(decode.value, validator)

  final def bsonValidate[A](decoder: BsonDecoder[A], validator: A => BsonDecoder.Result[A]): BsonDecoder[A] =
    (b: BsonValue) => decoder.apply(b).flatMap(validator)

  final def asStringEncoder[A](f: A => String): BsonEncoder[A] = a => BsonString(f(a))

  final def asStringDecoder[A](f: PartialFunction[String, A]): BsonDecoder[A] = b =>
    if (b.isString) {
      val str = b.asString().getValue
      f.andThen(Right.apply).orElse[String, BsonDecoder.Result[A]] {
        case s => Left(InvalidType(s))
      }.apply(str)
    } else {
      Left(InvalidType(b.toString))
    }

  final def map2ArrayEncoder[K, V](implicit e: BsonBiEncoder[K, V]): BsonEncoder[Map[K, V]] = t =>
    if (t.isEmpty) {
      BsonNull()
    } else {
      BsonArray(t.map(e.apply))
    }

  final def array2MapDecoder[K, V](implicit d: BsonBiDecoder[K, V]): BsonDecoder[Map[K, V]] = {
    case null => Right(Map.empty)
    case b: BsonValue if b.isArray =>
      val seq: List[BsonValue] = b.asArray().getValues.asScala.toList
      seq.traverse(d.apply).map(_.toMap)
    case b => Left(InvalidType(b))
  }


}
