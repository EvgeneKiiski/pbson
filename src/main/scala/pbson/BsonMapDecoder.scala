package pbson

/**
  * @author Evgenii Kiiski 
  */
trait BsonMapDecoder[K, V] extends BsonDecoder[(K, V)]
