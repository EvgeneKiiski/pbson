package pbson.tstring

import org.bson.BsonValue
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import pbson.{AnyBsonGen, BsonDecoder, BsonEncoder}

/**
  * @author Evgenii Kiiski 
  */
object StringSpecification extends Properties("String") {

  property("encoder decoder") = forAll { v: String =>
    BsonDecoder[String].apply(BsonEncoder[String].apply(v)) == Right(v)
  }

  property("decoder error") = forAll(AnyBsonGen.anyGen) { v: BsonValue =>
    v match {
      case null => BsonDecoder[String].apply(null).isLeft
      case s if s.isString => BsonDecoder[String].apply(s).isRight
      case s => BsonDecoder[String].apply(s).isLeft
    }
  }

}
