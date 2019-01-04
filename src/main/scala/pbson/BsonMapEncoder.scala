package pbson

/**
  * @author Evgenii Kiiski 
  */
trait BsonMapEncoder[K, V] extends BsonEncoder[(K, V)]