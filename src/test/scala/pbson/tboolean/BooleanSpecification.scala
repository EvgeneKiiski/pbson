package pbson.tboolean

import org.bson.BsonValue
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import pbson.{AnyBsonGen, BsonDecoder, BsonEncoder}

/**
  * @author Evgenii Kiiski 
  */
object BooleanSpecification extends Properties("Boolean") {

  property("encoder decoder") = forAll { v: Boolean =>
    BsonDecoder[Boolean].apply(BsonEncoder[Boolean].apply(v)) == Right(v)
  }

  property("decoder error") = forAll(AnyBsonGen.anyGen) { v: BsonValue =>
    v match {
      case s if s.isBoolean => BsonDecoder[Boolean].apply(s).isRight
      case s => BsonDecoder[Boolean].apply(s).isLeft
    }
  }

}
