package pbson.tdouble

import org.bson.BsonValue
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import pbson.{AnyBsonGen, BsonDecoder, BsonEncoder}

/**
  * @author Evgenii Kiiski 
  */
object DoubleSpecification extends Properties("Double") {

  property("encoder decoder") = forAll { v: Double =>
    BsonDecoder[Double].apply(BsonEncoder[Double].apply(v)) == Right(v)
  }

  property("decoder error") = forAll(AnyBsonGen.anyGen) { v: BsonValue =>
    v match {
      case null => BsonDecoder[Boolean].apply(null).isLeft
      case s if s.isDouble => BsonDecoder[Double].apply(s).isRight
      case s => BsonDecoder[Double].apply(s).isLeft
    }
  }

}
