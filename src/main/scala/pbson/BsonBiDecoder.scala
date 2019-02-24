package pbson

import org.bson.{BsonType, BsonValue}
import pbson.BsonError.UnexpectedType
import shapeless.{Lazy, Strict, Unwrapped}

/**
  * @author Evgenii Kiiski 
  */
abstract class BsonBiDecoder[K, V] {
  def apply(b: BsonValue): BsonDecoder.Result[(K, V)]
}

object BsonBiDecoder {

  // TODO dual behavior ???
  implicit final def biDecoderJoin[K, V, U](implicit
                                            uw: Strict[Unwrapped.Aux[K, U]],
                                            ke: Lazy[BsonDecoder[U]],
                                            vd: Lazy[BsonDecoder[V]]
                                           ): BsonBiDecoder[K, V] = { b =>
    if (b.isDocument) {
      val doc = b.asDocument()
      val key = if (doc.containsKey(BsonConst.Key)) {
        ke.value.apply(doc.get(BsonConst.Key)).map(k => uw.value.wrap(k))
      } else {
        ke.value.apply(doc).map(k => uw.value.wrap(k))
      }
      val value = if (doc.containsKey(BsonConst.Value)) {
        vd.value.apply(doc.get(BsonConst.Value))
      } else {
        vd.value.apply(doc)
      }
      key.flatMap(k => value.map((k, _)))
    } else {
      Left(UnexpectedType(b, BsonType.DOCUMENT))
    }
  }

}


