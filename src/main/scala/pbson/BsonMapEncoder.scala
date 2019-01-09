package pbson

import org.mongodb.scala.bson.BsonDocument

/**
  * @author Evgenii Kiiski 
  */
abstract class  BsonMapEncoder[K, V] extends BsonEncoder[(K, V)]

object BsonMapEncoder {
  implicit final def kvMapEncoder[K, V](implicit ke: BsonEncoder[K], ve: BsonEncoder[V]): BsonMapEncoder[K, V] = {
    case (k, v) => BsonDocument("k" -> ke(k), "v" -> ve(v))
  }
}