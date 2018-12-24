package pbson

import org.mongodb.scala.bson.BsonValue

/**
  * @author Evgenii Kiiski 
  */
trait BsonDecoder[E, A] {
  def apply(b: BsonValue): Either[E, A]
}
