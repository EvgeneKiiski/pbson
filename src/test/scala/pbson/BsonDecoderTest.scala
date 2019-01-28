package pbson

import org.mongodb.scala.bson.{BsonBoolean, BsonDouble, BsonInt32, BsonInt64, BsonNull, BsonString, BsonValue}
import org.scalacheck.Arbitrary._
import org.scalacheck.Prop
import org.scalacheck.Prop._
import org.scalatest.prop._
import org.scalatest.{Assertion, Matchers, ParallelTestExecution, WordSpec}
import pbson.BsonError.UnexpectedEmptyString
import pbson.semiauto._

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

  "Short decode" should {
    "some value" in {
      val v: BsonValue = BsonInt32(5)
      v.fromBson[Short] shouldEqual Right(5:Short)
    }
  }

  "Int decode" should {
    "some value" in {
      val v: BsonValue = BsonInt32(5)
      v.fromBson[Int] shouldEqual Right(5)
    }
  }

  "Long decode" should {
    "some value" in {
      val v: BsonValue = BsonInt64(5)
      v.fromBson[Long] shouldEqual Right(5)
    }
  }

  "Double decode" should {
    "some value" in {
      val v: BsonValue = BsonDouble(5.56)
      v.fromBson[Double] shouldEqual Right(5.56)
    }
  }

  "Float decode" should {
    "some value" in {
      val v: BsonValue = BsonDouble(5.56)
      v.fromBson[Float] shouldEqual Right(5.56f)
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

}
