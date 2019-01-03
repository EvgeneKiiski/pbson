package pbson.decoder

import org.mongodb.scala.bson.BsonValue
import pbson.BsonError.InvalidType
import pbson.{BsonDecoder, BsonError}
import shapeless.ops.record._
import shapeless._

/**
  * @author Evgenii Kiiski 
  */
abstract class DerivedBsonDecoder[A] extends BsonDecoder[A]

object DerivedBsonDecoder {

  implicit def deriveDecoder[A, R, K](implicit
                                   gen: LabelledGeneric.Aux[A, R],
                                   decode: Lazy[ReprBsonDecoder[R]]
                                  ): DerivedBsonDecoder[A] = new DerivedBsonDecoder[A] {
    override def apply(b: BsonValue): Either[BsonError, A] = {
      if (b.isDocument) {
        decode.value(b.asDocument()) match {
          case Right(r) => Right(gen.from(r))
          case l@Left(_) => l.asInstanceOf[Either[BsonError, A]]
        }
      } else {
        Left(InvalidType(s"type: ${b.getBsonType}"))
      }
    }
  }

}