package pbson

import org.mongodb.scala.bson.BsonDocument
import Const._

/**
  * @author Evgenii Kiiski 
  */
abstract class BsonMapEncoder[K, V] extends BsonEncoder[(K, V)]

object BsonMapEncoder {
  implicit final def kvMapEncoder[K, V](implicit ke: BsonEncoder[K], ve: BsonEncoder[V]): BsonMapEncoder[K, V] = {
    case (k, v) => {
      val body = ve(v)
      if (body.isDocument) {
        ve(v).asDocument().append(Key, ke(k))
      } else {
        BsonDocument(Key -> ke(k), Value -> ve(v))
      }
    }
  }
}
