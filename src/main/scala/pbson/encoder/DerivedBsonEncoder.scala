package pbson.encoder

import org.mongodb.scala.bson.{ BsonDocument, BsonNull, BsonValue }
import pbson.BsonEncoder
import shapeless._
//import ReprBsonEncoder._

import scala.language.experimental.macros

/**
  * @author Evgenii Kiiski 
  */
abstract class DerivedBsonEncoder[A] extends BsonEncoder[A]

object DerivedBsonEncoder {

  implicit final def deriveEncoder[A, R](
    implicit
    gen: LabelledGeneric.Aux[A, R],
    encode: Lazy[ReprBsonEncoder[R]]
  ): DerivedBsonEncoder[A] = new DerivedBsonEncoder[A] {
    final def apply(t: A): BsonValue = {
      BsonDocument(
        encode.value.apply(gen.to(t)).filterNot {
          case (_, v) if v == BsonNull() => true
          case _ => false
        }
      )
    }
  }

}
