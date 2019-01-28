package pbson

import org.bson.BsonNull
import org.mongodb.scala.bson.{BsonBoolean, BsonDouble, BsonInt32, BsonInt64, BsonString}
import org.scalatest.{Matchers, ParallelTestExecution, WordSpec}

import scala.xml.Null

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
  }

  "Char encode" should {
    "some string" in {
      val v : Char = 'a'
      v.toBson shouldEqual BsonString("a")
    }
  }

  "Short encode" should {
    "same value" in {
      val v : Short = 4
      v.toBson shouldEqual BsonInt32(4)
    }
  }

  "Int encode" should {
    "same value" in {
      val v : Int = 4
      v.toBson shouldEqual BsonInt32(4)
    }
  }

  "Long encode" should {
    "same value" in {
      val v : Long = 4
      v.toBson shouldEqual BsonInt64(4)
    }
  }

  "Double encode" should {
    "same value" in {
      val v : Double = 4.6
      ~=(v.toBson.asDouble().doubleValue(),4.6, 0.0001) shouldEqual true
    }
  }

  "Float encode" should {
    "same value" in {
      val v : Float = 4.6f
      ~=(v.toBson.asDouble().doubleValue(),4.6, 0.0001) shouldEqual true
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

}
