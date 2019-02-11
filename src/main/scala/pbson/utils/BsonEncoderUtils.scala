package pbson.utils

import org.mongodb.scala.bson.{BsonArray, BsonNull, BsonString}
import pbson.{BsonBiEncoder, BsonEncoder}

/**
  * @author Evgenii Kiiski 
  */
trait BsonEncoderUtils {

  final def asStringEncoder[A](f: A => String): BsonEncoder[A] = a => BsonString(f(a))

  final def map2ArrayEncoder[K, V](implicit e: BsonBiEncoder[K, V]): BsonEncoder[Map[K, V]] = t =>
    if (t.isEmpty) {
      BsonNull()
    } else {
      BsonArray(t.map(e.apply))
    }

  final def enumEncoder[E <: Enumeration](enum: E): BsonEncoder[E#Value] = e =>
    BsonEncoder.stringEncoder(e.toString)

}
