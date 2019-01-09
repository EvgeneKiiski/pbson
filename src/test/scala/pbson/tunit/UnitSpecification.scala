package pbson.tunit

import org.bson.BsonValue
import org.mongodb.scala.bson.BsonNull
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import pbson.{AnyBsonGen, BsonDecoder, BsonEncoder}

/**
  * @author Evgenii Kiiski 
  */
object UnitSpecification extends Properties("Unit") {

  property("encoder decoder") = forAll { v: Unit =>
    BsonDecoder[Unit].apply(BsonEncoder[Unit].apply(v)) == Right(v)
  }

  property("decoder error") = forAll(AnyBsonGen.anyGen) { v: BsonValue =>
    v match {
      case null => BsonDecoder[Unit].apply(null).isLeft
      case s if s == BsonNull() => BsonDecoder[Unit].apply(s).isRight
      case s => BsonDecoder[Unit].apply(s).isLeft
    }
  }

}
