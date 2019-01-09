package pbson.tfloat

import org.bson.BsonValue
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import pbson.{AnyBsonGen, BsonDecoder, BsonEncoder}

/**
  * @author Evgenii Kiiski 
  */
object FloatSpecification extends Properties("Float") {

  property("encoder decoder") = forAll { v: Float =>
    BsonDecoder[Float].apply(BsonEncoder[Float].apply(v)) == Right(v)
  }

  property("decoder error") = forAll(AnyBsonGen.anyGen) { v: BsonValue =>
    v match {
      case null => BsonDecoder[Boolean].apply(null).isLeft
      case s if s.isDouble => BsonDecoder[Float].apply(s).isRight
      case s => BsonDecoder[Float].apply(s).isLeft
    }
  }

}
