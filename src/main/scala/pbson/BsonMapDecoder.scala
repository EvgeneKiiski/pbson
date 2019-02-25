package pbson

import org.bson.BsonValue
import shapeless.{Lazy, Strict, Unwrapped}

/**
  * @author Evgenii Kiiski 
  */
abstract class BsonMapDecoder[K, V] {
  def apply(b: (String, BsonValue)): BsonDecoder.Result[(K, V)]
}

object BsonMapDecoder {

  implicit final def kvMapDecoder[K, V](implicit
                                        uw: Strict[Unwrapped.Aux[K, String]],
                                        vd: Lazy[BsonDecoder[V]]
                                       ): BsonMapDecoder[K, V] = {
    case (k, v) => vd.value.apply(v).map(uw.value.wrap(k) -> _)
  }

}
