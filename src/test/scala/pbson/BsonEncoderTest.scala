package pbson

import java.util.UUID

import org.bson.types.Decimal128
import org.mongodb.scala.bson.{BsonBoolean, BsonDateTime, BsonDecimal128, BsonInt32, BsonInt64, BsonNull, BsonString}
import org.scalatest.{Matchers, ParallelTestExecution, WordSpec}

/**
  * @author Evgenii Kiiski 
  */
class BsonEncoderTest extends WordSpec with ParallelTestExecution with Matchers {

  def ~=(x: Double, y: Double, precision: Double): Boolean = {
    if ((x - y).abs < precision) true else false
  }

  "Unit encode" should {
    "()" in {
      val v : Unit = ()
      v.toBson.isInstanceOf[BsonNull] shouldEqual true
    }
  }

  "String encode" should {
    "empty string" in {
      val v : String = ""
      v.toBson shouldEqual BsonString("")
    }
    "some string" in {
      val v : String = "fefsdfs"
      v.toBson shouldEqual BsonString("fefsdfs")
    }
    "null string" in {
      val v : String = null
      v.toBson.isNull shouldEqual true
    }
  }

  "Char encode" should {
    "some char" in {
      val v : Char = 'a'
      v.toBson shouldEqual BsonString("a")
    }
  }

  "java.lang.Character encode" should {
    "some java.lang.Character" in {
      val v : java.lang.Character = 'a'
      v.toBson shouldEqual BsonString("a")
    }
    "null java.lang.Character" in {
      val v : java.lang.Character = null
      v.toBson.isNull shouldEqual true
    }
  }

  "Short encode" should {
    "same value" in {
      val v : Short = 4
      v.toBson shouldEqual BsonInt32(4)
    }
  }

  "java.lang.Short encode" should {
    "same value" in {
      val v : java.lang.Short = 4.toShort
      v.toBson shouldEqual BsonInt32(4)
    }
    "null value" in {
      val v : java.lang.Short = null
      v.toBson.isNull shouldEqual true
    }
  }

  "Int encode" should {
    "same value" in {
      val v : Int = 4
      v.toBson shouldEqual BsonInt32(4)
    }
  }

  "java.lang.Integer encode" should {
    "same value" in {
      val v : java.lang.Integer = 4
      v.toBson shouldEqual BsonInt32(4)
    }
    "null value" in {
      val v : java.lang.Integer = null
      v.toBson.isNull shouldEqual true
    }
  }

  "Long encode" should {
    "same value" in {
      val v : Long = 4
      v.toBson shouldEqual BsonInt64(4)
    }
  }

  "java.lang.Long encode" should {
    "same value" in {
      val v : java.lang.Long = 4l
      v.toBson shouldEqual BsonInt64(4)
    }
    "null value" in {
      val v : java.lang.Long = null
      v.toBson.isNull shouldEqual true
    }
  }

  "Double encode" should {
    "same value" in {
      val v : Double = 4.6
      ~=(v.toBson.asDouble().doubleValue(),4.6, 0.0001) shouldEqual true
    }
  }

  "java.lang.Double encode" should {
    "same value" in {
      val v : java.lang.Double = 4.6
      ~=(v.toBson.asDouble().doubleValue(),4.6, 0.0001) shouldEqual true
    }
    "null value" in {
      val v : java.lang.Double = null
      v.toBson.isNull shouldEqual true
    }
  }

  "Float encode" should {
    "same value" in {
      val v : Float = 4.6f
      ~=(v.toBson.asDouble().doubleValue(),4.6, 0.0001) shouldEqual true
    }
  }

  "java.lang.Float encode" should {
    "same value" in {
      val v : java.lang.Float = 4.6f
      ~=(v.toBson.asDouble().doubleValue(),4.6, 0.0001) shouldEqual true
    }
    "null value" in {
      val v : java.lang.Float = null
      v.toBson.isNull shouldEqual true
    }
  }

  "Boolean encode" should {
    "true" in {
      val v : Boolean = true
      v.toBson shouldEqual BsonBoolean(true)
    }
    "false" in {
      val v : Boolean = false
      v.toBson shouldEqual BsonBoolean(false)
    }
  }

  "java.lang.Boolean encode" should {
    "true" in {
      val v : java.lang.Boolean = true
      v.toBson shouldEqual BsonBoolean(true)
    }
    "false" in {
      val v : java.lang.Boolean = false
      v.toBson shouldEqual BsonBoolean(false)
    }
    "null value" in {
      val v : java.lang.Boolean = null
      v.toBson.isNull shouldEqual true
    }
  }

  "UUID encode" should {
    "same value" in {
      val v : UUID = UUID.randomUUID()
      v.toBson shouldEqual BsonString(v.toString)
    }
  }

  "Decimal128 encode" should {
    "same value" in {
      val v : Decimal128 = Decimal128.parse("5.56")
      v.toBson shouldEqual BsonDecimal128(5.56)
    }
    "null value" in {
      val v : Decimal128 = null
      v.toBson.isNull shouldEqual true
    }
  }

  "java.util.Date encode" should {
    "same value" in {
      val v : java.util.Date = new java.util.Date()
      v.toBson shouldEqual BsonDateTime(v.getTime)
    }
    "null value" in {
      val v : java.util.Date = null
      v.toBson.isNull shouldEqual true
    }
  }

}
