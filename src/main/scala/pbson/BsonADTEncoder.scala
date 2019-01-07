package pbson

import org.mongodb.scala.bson.BsonValue

/**
  * @author Evgenii Kiiski 
  */
trait BsonADTEncoder[K, V] {
  def apply(name: String, t: BsonValue): List[(String, BsonValue)]
}
