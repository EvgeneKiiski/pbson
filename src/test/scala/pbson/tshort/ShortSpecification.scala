package pbson.tshort

import org.bson.BsonValue
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import pbson.{AnyBsonGen, BsonDecoder, BsonEncoder}

/**
  * @author Evgenii Kiiski 
  */
object ShortSpecification extends Properties("Short") {

  property("encoder decoder") = forAll { v: Short =>
    BsonDecoder[Short].apply(BsonEncoder[Short].apply(v)) == Right(v)
  }

  property("decoder error") = forAll(AnyBsonGen.anyGen) { v: BsonValue =>
    v match {
      case null => BsonDecoder[Short].apply(null).isLeft
      case s if s.isInt32 => BsonDecoder[Short].apply(s).isRight
      case s if s.isInt64 => BsonDecoder[Short].apply(s).isRight
      case s => BsonDecoder[Short].apply(s).isLeft
    }
  }

}
