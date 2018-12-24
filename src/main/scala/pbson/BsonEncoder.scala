package pbson

import org.mongodb.scala.bson.BsonValue

/**
  * @author Evgenii Kiiski 
  */
trait BsonEncoder[A] {
  def apply(t: A): BsonValue
}
