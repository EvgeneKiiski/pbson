package pbson

import org.bson.BsonValue
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import org.scalatest.EitherValues

/**
  * @author Evgenii Kiiski 
  */
object BsonDecoderNotSupportSpecification extends Properties("Not support types ") with EitherValues {

  property("Unit") = forAll(AnyBsonGen.anyGen.filterNot(_.isNull)) { v: BsonValue =>
    BsonDecoder[Unit].apply(v).left.value.isInstanceOf[BsonError.InvalidType]
  }

  property("String") = forAll(AnyBsonGen.anyGen.filterNot(_.isString)) { v: BsonValue =>
    BsonDecoder[String].apply(v).left.value.isInstanceOf[BsonError.InvalidType]
  }

  property("Char") = forAll(AnyBsonGen.anyGen.filterNot(_.isString)) { v: BsonValue =>
    BsonDecoder[Char].apply(v).left.value.isInstanceOf[BsonError.InvalidType]
  }

  property("Short") = forAll(AnyBsonGen.anyGen.filterNot(_.isInt32)) { v: BsonValue =>
    BsonDecoder[Short].apply(v).left.value.isInstanceOf[BsonError.InvalidType]
  }

  property("Int") = forAll(AnyBsonGen.anyGen.filterNot(_.isInt32)) { v: BsonValue =>
    BsonDecoder[Int].apply(v).left.value.isInstanceOf[BsonError.InvalidType]
  }

  property("Long") = forAll(AnyBsonGen.anyGen.filterNot(_.isInt64)) { v: BsonValue =>
    BsonDecoder[Long].apply(v).left.value.isInstanceOf[BsonError.InvalidType]
  }

  property("Float") = forAll(AnyBsonGen.anyGen.filterNot(_.isDouble)) { v: BsonValue =>
    BsonDecoder[Float].apply(v).left.value.isInstanceOf[BsonError.InvalidType]
  }

  property("Double") = forAll(AnyBsonGen.anyGen.filterNot(_.isDouble)) { v: BsonValue =>
    BsonDecoder[Double].apply(v).left.value.isInstanceOf[BsonError.InvalidType]
  }

  property("Boolean") = forAll(AnyBsonGen.anyGen.filterNot(_.isBoolean)) { v: BsonValue =>
    BsonDecoder[Boolean].apply(v).left.value.isInstanceOf[BsonError.InvalidType]
  }

}
