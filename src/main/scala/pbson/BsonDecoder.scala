package pbson

import org.mongodb.scala.bson.BsonValue

/**
  * @author Evgenii Kiiski 
  */
trait BsonDecoder[A] {
  def apply(b: BsonValue): Either[BsonError, A]
}
