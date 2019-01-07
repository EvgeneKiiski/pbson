package pbson

import org.mongodb.scala.bson.{BsonString, BsonValue}
import pbson.BsonError.InvalidType
import pbson.decoder.DerivedBsonDecoder
import pbson.encoder.DerivedBsonEncoder
import shapeless._

/**
  * @author Evgenii Kiiski 
  */
object semiauto {

  final def deriveEncoder[A](implicit encode: Lazy[DerivedBsonEncoder[A]]): BsonEncoder[A] = encode.value

  final def deriveDecoder[A](implicit decode: Lazy[DerivedBsonDecoder[A]]): BsonDecoder[A] = decode.value

  final def mapKeyHintEncoder[K, V, U](name: String)(implicit
                                                     uw: Strict[Unwrapped.Aux[K, U]],
                                                     ke: Lazy[BsonEncoder[U]],
                                                     ve: BsonEncoder[V]
  ): BsonMapEncoder[K, V] = {
    case (k, v) => ve(v).asDocument().append(name, ke.value(uw.value.unwrap(k)))
  }

  final def mapKeyHintDecoder[K, V, U](name: String)(implicit
                                                     uw: Strict[Unwrapped.Aux[K, U]],
                                                     kd: Lazy[BsonDecoder[U]],
                                                     vd: BsonDecoder[V]
  ): BsonMapDecoder[K, V] = {
    case b: BsonValue if b.isDocument =>
      val doc = b.asDocument()
      val key = doc.get(name)
      if (key != null) {
        kd.value(key).map(k => uw.value.wrap(k)).flatMap(k => vd(doc).map(v => (k, v)))
      } else {
        Left(InvalidType(s" ${doc.toJson} expected k,v"))
      }
    case b => Left(InvalidType(b.toString))
  }

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


}
