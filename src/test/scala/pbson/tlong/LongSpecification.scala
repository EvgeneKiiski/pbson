package pbson.tlong

import org.bson.BsonValue
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import pbson.{AnyBsonGen, BsonDecoder, BsonEncoder}

/**
  * @author Evgenii Kiiski 
  */
object LongSpecification extends Properties("Long") {

  property("encoder decoder") = forAll { v: Int =>
    BsonDecoder[Long].apply(BsonEncoder[Long].apply(v)) == Right(v)
  }

  property("decoder error") = forAll(AnyBsonGen.anyGen) { v: BsonValue =>
    v match {
      case null => BsonDecoder[Boolean].apply(null).isLeft
      case s if s.isInt32 => BsonDecoder[Long].apply(s).isRight
      case s if s.isInt64 => BsonDecoder[Long].apply(s).isRight
      case s => BsonDecoder[Long].apply(s).isLeft
    }
  }

}
