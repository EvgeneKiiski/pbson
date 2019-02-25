package pbson

import org.bson.BsonValue
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties
import org.scalatest.EitherValues
import pbson.gen.AnyBsonGen

/**
  * @author Evgenii Kiiski 
  */
object BsonDecoderNotSupportSpecification extends Properties("Not support types ") with EitherValues {

  property("Unit") = forAll(AnyBsonGen.anyBsonGen.filterNot(_.isNull)) { v: BsonValue =>
    BsonDecoder[Unit].apply(v).left.value.isInstanceOf[BsonError.UnexpectedType]
  }

  property("String") = forAll(AnyBsonGen.anyBsonGen.filterNot(_.isString)) { v: BsonValue =>
    BsonDecoder[String].apply(v).left.value.isInstanceOf[BsonError.UnexpectedType]
  }

  property("Char") = forAll(AnyBsonGen.anyBsonGen.filterNot(_.isString)) { v: BsonValue =>
    BsonDecoder[Char].apply(v).left.value.isInstanceOf[BsonError.UnexpectedType]
  }

  property("Short") = forAll(AnyBsonGen.anyBsonGen.filterNot(_.isInt32)) { v: BsonValue =>
    BsonDecoder[Short].apply(v).left.value.isInstanceOf[BsonError.UnexpectedType]
  }

  property("Int") = forAll(AnyBsonGen.anyBsonGen.filterNot(_.isInt32)) { v: BsonValue =>
    BsonDecoder[Int].apply(v).left.value.isInstanceOf[BsonError.UnexpectedType]
  }

  property("Long") = forAll(AnyBsonGen.anyBsonGen.filterNot(_.isInt64)) { v: BsonValue =>
    BsonDecoder[Long].apply(v).left.value.isInstanceOf[BsonError.UnexpectedType]
  }

  property("Float") = forAll(AnyBsonGen.anyBsonGen.filterNot(_.isDouble)) { v: BsonValue =>
    BsonDecoder[Float].apply(v).left.value.isInstanceOf[BsonError.UnexpectedType]
  }

  property("Double") = forAll(AnyBsonGen.anyBsonGen.filterNot(_.isDouble)) { v: BsonValue =>
    BsonDecoder[Double].apply(v).left.value.isInstanceOf[BsonError.UnexpectedType]
  }

  property("Boolean") = forAll(AnyBsonGen.anyBsonGen.filterNot(_.isBoolean)) { v: BsonValue =>
    BsonDecoder[Boolean].apply(v).left.value.isInstanceOf[BsonError.UnexpectedType]
  }

}
