package pbson.tchar

import org.bson.BsonValue
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import pbson.{AnyBsonGen, BsonDecoder, BsonEncoder}

/**
  * @author Evgenii Kiiski 
  */
object CharSpecification extends Properties("Char") {

  property("encoder decoder") = forAll { v: Char =>
    BsonDecoder[Char].apply(BsonEncoder[Char].apply(v)) == Right(v)
  }

  property("decoder error") = forAll(AnyBsonGen.anyGen) { v: BsonValue =>
    v match {
      case null => BsonDecoder[Char].apply(null).isLeft
      case s if s.isString && s.asString().getValue.length == 1 => BsonDecoder[Char].apply(s).isRight
      case s => BsonDecoder[Char].apply(s).isLeft
    }
  }

}
