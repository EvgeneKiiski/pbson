package pbson

import java.time.Instant
import java.util.UUID

import org.bson.BsonBinary
import org.bson.types.Decimal128
import org.mongodb.scala.bson._
import org.scalatestplus.scalacheck.Checkers
import org.scalatest.{Matchers, WordSpec}
import pbson.BsonError.UnexpectedEmptyString

/**
  * @author Evgenii Kiiski 
  */
class BsonDecoderTest extends WordSpec with Matchers with Checkers {

  "Unit decode" should {
    "()" in {
      val v: BsonValue = BsonNull()
      v.fromBson[Unit] shouldEqual Right(())
    }
  }

  "String decode" should {
    "empty string" in {
      val v: BsonValue = BsonString("")
      v.fromBson[String] shouldEqual Right("")
    }
    "some string" in {
      val v: BsonValue = BsonString("sadadsa")
      v.fromBson[String] shouldEqual Right("sadadsa")
    }
    "BsonNull" in {
      val v: BsonValue = BsonNull()
      v.fromBson[String] shouldEqual Right(null)
    }
  }

  "Char decode" should {
    "some string" in {
      val v: BsonValue = BsonString("s")
      v.fromBson[Char] shouldEqual Right('s')
    }
    "long string first char" in {
      val v: BsonValue = BsonString("swerw")
      v.fromBson[Char] shouldEqual Right('s')
    }
    "empty string" in {
      val v: BsonValue = BsonString("")
      v.fromBson[Char] shouldEqual Left(UnexpectedEmptyString)
    }
  }

  "java.lang.Char decode" should {
    "some string" in {
      val v: BsonValue = BsonString("s")
      v.fromBson[java.lang.Character] shouldEqual Right('s')
    }
    "long string first char" in {
      val v: BsonValue = BsonString("swerw")
      v.fromBson[java.lang.Character] shouldEqual Right('s')
    }
    "empty string" in {
      val v: BsonValue = BsonString("")
      v.fromBson[java.lang.Character] shouldEqual Left(UnexpectedEmptyString)
    }
    "BsonNull" in {
      val v: BsonValue = BsonNull()
      v.fromBson[java.lang.Character] shouldEqual Right(null)
    }
  }

  "Short decode" should {
    "some value" in {
      val v: BsonValue = BsonInt32(5)
      v.fromBson[Short] shouldEqual Right(5:Short)
    }
  }

  "java.lang.Short decode" should {
    "some value" in {
      val v: BsonValue = BsonInt32(5)
      v.fromBson[java.lang.Short] shouldEqual Right(5:Short)
    }
    "BsonNull" in {
      val v: BsonValue = BsonNull()
      v.fromBson[java.lang.Short] shouldEqual Right(null)
    }
  }

  "Int decode" should {
    "some value" in {
      val v: BsonValue = BsonInt32(5)
      v.fromBson[Int] shouldEqual Right(5)
    }
  }

  "java.lang.Integer decode" should {
    "some value" in {
      val v: BsonValue = BsonInt32(5)
      v.fromBson[java.lang.Integer] shouldEqual Right(5)
    }
    "BsonNull" in {
      val v: BsonValue = BsonNull()
      v.fromBson[java.lang.Integer] shouldEqual Right(null)
    }
  }

  "Long decode" should {
    "some value" in {
      val v: BsonValue = BsonInt64(5)
      v.fromBson[Long] shouldEqual Right(5)
    }
  }

  "java.lang.Long decode" should {
    "some value" in {
      val v: BsonValue = BsonInt64(5)
      v.fromBson[java.lang.Long] shouldEqual Right(5)
    }
    "BsonNull" in {
      val v: BsonValue = BsonNull()
      v.fromBson[java.lang.Long] shouldEqual Right(null)
    }
  }

  "Double decode" should {
    "some value" in {
      val v: BsonValue = BsonDouble(5.56)
      v.fromBson[Double] shouldEqual Right(5.56)
    }
  }

  "java.lang.Double decode" should {
    "some value" in {
      val v: BsonValue = BsonDouble(5.56)
      v.fromBson[java.lang.Double] shouldEqual Right(5.56)
    }
    "BsonNull" in {
      val v: BsonValue = BsonNull()
      v.fromBson[java.lang.Double] shouldEqual Right(null)
    }
  }

  "Float decode" should {
    "some value" in {
      val v: BsonValue = BsonDouble(5.56)
      v.fromBson[Float] shouldEqual Right(5.56f)
    }
  }

  "java.lang.Float decode" should {
    "some value" in {
      val v: BsonValue = BsonDouble(5.56)
      v.fromBson[java.lang.Float] shouldEqual Right(5.56f)
    }
    "BsonNull" in {
      val v: BsonValue = BsonNull()
      v.fromBson[java.lang.Float] shouldEqual Right(null)
    }
  }

  "Boolean decode" should {
    "true" in {
      val v: BsonValue = BsonBoolean(true)
      v.fromBson[Boolean] shouldEqual Right(true)
    }
    "false" in {
      val v: BsonValue = BsonBoolean(false)
      v.fromBson[Boolean] shouldEqual Right(false)
    }
  }

  "java.lang.Boolean decode" should {
    "true" in {
      val v: BsonValue = BsonBoolean(true)
      v.fromBson[java.lang.Boolean] shouldEqual Right(true)
    }
    "false" in {
      val v: BsonValue = BsonBoolean(false)
      v.fromBson[java.lang.Boolean] shouldEqual Right(false)
    }
    "BsonNull" in {
      val v: BsonValue = BsonNull()
      v.fromBson[java.lang.Boolean] shouldEqual Right(null)
    }
  }

  "UUID decode" should {
    "some uuid" in {
      val u = UUID.randomUUID()
      val v: BsonValue = new BsonBinary(u)
      v.fromBson[UUID] shouldEqual Right(u)
    }
    "invalid value" in {
      val v: BsonValue = BsonString("012345678901234567890123456789012345")
      v.asString().getValue.length equals 36
      v.fromBson[UUID].isLeft shouldEqual true
    }
    "invalid value int" in {
      val v: BsonValue = new BsonBinary(Array.emptyByteArray)
      v.fromBson[UUID].isLeft shouldEqual true
    }
    "BsonNull" in {
      val v: BsonValue = BsonNull()
      v.fromBson[UUID] shouldEqual Right(null)
    }
  }

  "Decimal128 decode" should {
    "some value" in {
      val v: BsonValue = BsonDecimal128(5.56)
      v.fromBson[Decimal128] shouldEqual Right(Decimal128.parse("5.56"))
    }
    "invalid value int" in {
      val v: BsonValue = BsonInt32(12)
      v.fromBson[Decimal128].isLeft shouldEqual true
    }
    "BsonNull" in {
      val v: BsonValue = BsonNull()
      v.fromBson[Decimal128] shouldEqual Right(null)
    }
  }

  "java.utilDate decode" should {
    "some value" in {
      val v: BsonValue = BsonDateTime(5L)
      v.fromBson[java.util.Date] shouldEqual Right(java.util.Date.from(Instant.ofEpochMilli(5L)))
    }
    "invalid value int" in {
      val v: BsonValue = BsonInt32(12)
      v.fromBson[java.util.Date].isLeft shouldEqual true
    }
    "BsonNull" in {
      val v: BsonValue = BsonNull()
      v.fromBson[java.util.Date] shouldEqual Right(null)
    }
  }

  "BsonDecoder" should {
    "handleErrorWith left" in {
      val decoder = new BsonDecoder[Int] {
        override def apply(b: BsonValue): BsonDecoder.Result[Int] = Left(BsonError.Error)
      }
      val f: BsonError => BsonDecoder[Int] = _ => _ => Right(2)
      decoder.handleErrorWith(f)(BsonString("1")) shouldEqual Right(2)
    }
    "handleErrorWith right" in {
      val decoder = new BsonDecoder[Int] {
        override def apply(b: BsonValue): BsonDecoder.Result[Int] = Right(5)
      }
      val f: BsonError => BsonDecoder[Int] = _ => _ => Right(2)
      decoder.handleErrorWith(f)(BsonString("1")) shouldEqual Right(5)
    }
    "product right, right" in {
      val decoder1 = new BsonDecoder[Int] {
        override def apply(b: BsonValue): BsonDecoder.Result[Int] = Right(5)
      }
      val decoder2 = new BsonDecoder[Int] {
        override def apply(b: BsonValue): BsonDecoder.Result[Int] = Right(8)
      }
      decoder1.product(decoder2)(BsonString("1")) shouldEqual Right((5, 8))
    }
    "product right, left" in {
      val decoder1 = new BsonDecoder[Int] {
        override def apply(b: BsonValue): BsonDecoder.Result[Int] = Right(5)
      }
      val decoder2 = new BsonDecoder[Int] {
        override def apply(b: BsonValue): BsonDecoder.Result[Int] = Left(BsonError.Error)
      }
      decoder1.product(decoder2)(BsonString("1")) shouldEqual Left(BsonError.Error)
    }
    "product left, right" in {
      val decoder1 = new BsonDecoder[Int] {
        override def apply(b: BsonValue): BsonDecoder.Result[Int] = Left(BsonError.Error)
      }
      val decoder2 = new BsonDecoder[Int] {
        override def apply(b: BsonValue): BsonDecoder.Result[Int] = Right(8)
      }
      decoder1.product(decoder2)(BsonString("1")) shouldEqual Left(BsonError.Error)
    }
    "product left, left" in {
      val decoder1 = new BsonDecoder[Int] {
        override def apply(b: BsonValue): BsonDecoder.Result[Int] = Left(BsonError.Error)
      }
      val decoder2 = new BsonDecoder[Int] {
        override def apply(b: BsonValue): BsonDecoder.Result[Int] = Left(BsonError.UnexpectedEmptyString)
      }
      decoder1.product(decoder2)(BsonString("1")) shouldEqual Left(BsonError.Error)
    }
  }

}
