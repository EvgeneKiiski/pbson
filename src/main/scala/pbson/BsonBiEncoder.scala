package pbson

import org.bson.{BsonDocument, BsonValue}
import shapeless.{Lazy, Strict, Unwrapped}

import scala.collection.JavaConverters._

/**
  * @author Evgenii Kiiski 
  */
abstract class BsonBiEncoder[K, V] {
  def apply(t: (K, V)): BsonValue
}

object BsonBiEncoder {

  implicit final def biEncoderJoin[K, V, U](implicit
                                            uw: Strict[Unwrapped.Aux[K, U]],
                                            ke: Lazy[BsonEncoder[U]],
                                            ve: Lazy[BsonEncoder[V]]
                                           ): BsonBiEncoder[K, V] = {
    case (k, v) =>
      val key = ke.value.apply(uw.value.unwrap(k))
      val value = ve.value.apply(v)
      if (key.isDocument && value.isDocument) {
        val doc = key.asDocument()
        val iterator = value.asDocument().entrySet().iterator()
        while (iterator.hasNext) {
          val v = iterator.next()
          doc.append(v.getKey, v.getValue)
        }
        doc
      } else if (key.isDocument && !value.isDocument) {
        val doc = key.asDocument()
        doc.append(BsonConst.Value, value)
        doc
      } else if (!key.isDocument && value.isDocument) {
        val doc = value.asDocument()
        doc.append(BsonConst.Key, key)
        doc
      } else {
        val doc = new BsonDocument(BsonConst.Key, key)
        doc.append(BsonConst.Value, value)
        doc
      }
  }

}


