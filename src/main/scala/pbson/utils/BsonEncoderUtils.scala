package pbson.utils

import java.util

import org.bson.{BsonArray, BsonString, BsonValue}
import pbson.{BsonBiEncoder, BsonEncoder}

import scala.collection.JavaConverters._

/**
  * @author Evgenii Kiiski 
  */
trait BsonEncoderUtils {

  final def asStringEncoder[A](f: A => String): BsonEncoder[A] = a => new BsonString(f(a))

  final def map2ArrayEncoder[K, V](implicit e: BsonBiEncoder[K, V]): BsonEncoder[Map[K, V]] = t =>
    if (t.isEmpty) {
      BsonEncoder.BSON_NULL
    } else {
      val list = new util.ArrayList[BsonValue](t.map(e.apply).asJavaCollection)
      new BsonArray(list)
    }

  final def enumEncoder[E <: Enumeration](enum: E): BsonEncoder[E#Value] = e =>
    BsonEncoder.stringEncoder(e.toString)

}
