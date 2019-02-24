package pbson

import org.bson.BsonValue
import shapeless.{Lazy, Strict, Unwrapped}

/**
  * @author Evgenii Kiiski 
  */
abstract class BsonMapEncoder[K, V] {
  def apply(t: (K, V)): (String, BsonValue)
}

object BsonMapEncoder {

  implicit final def kvMapEncoder[K, V](implicit
                                        uw: Strict[Unwrapped.Aux[K, String]],
                                        ve: Lazy[BsonEncoder[V]]
                                       ): BsonMapEncoder[K, V] = {
        case (k, v) => uw.value.unwrap(k) -> ve.value.apply(v)
    }

}
