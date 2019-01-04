package pbson

import org.mongodb.scala.bson.BsonValue

/**
  * @author Evgenii Kiiski 
  */
trait BsonDecoder[A] {
  def apply(b: BsonValue): BsonDecoder.Result[A]
}

object BsonDecoder {
  type Result[A] = Either[BsonError, A]
}
