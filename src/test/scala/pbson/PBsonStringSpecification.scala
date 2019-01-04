package pbson

import org.mongodb.scala.bson.BsonValue
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

/**
  * @author Evgenii Kiiski 
  */
object PBsonStringSpecification extends Properties("String") {

  property("encoder decoder") = forAll { v: String =>
    def test(s: String)(implicit ev: BsonEncoder[String], dv: BsonDecoder[String]): Boolean = {
      dv(ev(s)) == Right(s)
    }
    test(v)
  }

//  property("decoder error") = forAll(AnyBsonGen.anyGen) { v: BsonValue =>
//    def test(b: BsonValue)(implicit dv: BsonDecoder[String]): Boolean = b match {
//      case s if s == null => dv(null).isLeft
//      case s if s.isString => dv(s).isRight
//      case s => dv(s).isLeft
//    }
//    test(v)
//  }

}
