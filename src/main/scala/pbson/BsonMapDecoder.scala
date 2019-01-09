package pbson

import org.mongodb.scala.bson.BsonValue
import pbson.BsonError.InvalidType
import Const._

/**
  * @author Evgenii Kiiski 
  */
abstract class BsonMapDecoder[K, V] extends BsonDecoder[(K, V)]

object BsonMapDecoder {

  implicit final def kvMapDecoder[K, V](implicit kd: BsonDecoder[K], vd: BsonDecoder[V]): BsonMapDecoder[K, V] = {
    case b: BsonValue if b.isDocument =>
      val doc = b.asDocument()
      val key = doc.get(Key)
      if (key != null) {
        if(doc.containsKey(Value)) {
          val value = doc.get(Value)
          kd(key).flatMap(k => vd(value).map(v => (k, v)))
        } else {
          kd(key).flatMap(k => vd(doc).map(v => (k, v)))
        }
      } else {
        Left(InvalidType(s" ${doc.toJson} expected k,v"))
      }
    case b => Left(InvalidType(b.toString))
  }

}
