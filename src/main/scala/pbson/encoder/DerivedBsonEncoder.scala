package pbson.encoder

import org.mongodb.scala.bson.{BsonDocument, BsonValue}
import pbson.BsonEncoder
import shapeless.Lazy

import scala.language.experimental.macros

/**
  * @author Evgenii Kiiski 
  */
abstract class DerivedBsonEncoder[A] extends BsonEncoder[A]

object DerivedBsonEncoder {
  implicit def derivedBsonEncoder[A]: DerivedBsonEncoder[A] = macro Deriver.deriveEncoder[A]
//  implicit def derivedBsonEncoder[A]: DerivedBsonEncoder[A] = new DerivedBsonEncoder[A] {
//    override def apply(t: A): BsonValue = {
//      val aw = implicitly[BsonEncoder[Int]]
//      val bw = implicitly[BsonEncoder[String]]
//      BsonDocument()
//    }
//  }
}
