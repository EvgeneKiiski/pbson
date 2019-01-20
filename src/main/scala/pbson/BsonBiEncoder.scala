package pbson

import org.mongodb.scala.bson.{BsonDocument, BsonValue}
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
      BsonDocument(value2doc(value, BsonConst.Value).asScala ++ value2doc(key, BsonConst.Key).asScala)
  }

  private def value2doc(b: BsonValue, key: => String): BsonDocument = {
    if (b.isDocument) {
      b.asDocument()
    } else {
      BsonDocument(key -> b)
    }
  }

}


