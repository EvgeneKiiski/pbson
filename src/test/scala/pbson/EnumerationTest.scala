package pbson

import org.mongodb.scala.bson.{ BsonString, BsonValue }
import org.scalatest.{ Matchers, WordSpec }

/**
  * @author Eugene Kiyski
  */
class EnumerationTest extends WordSpec with Matchers {
  import EnumerationTest._

  implicit val weekDayEncoder: BsonEncoder[EnumerationTest.WeekDay.Value] = enumEncoder(WeekDay)
  implicit val weekDayDecoder: BsonDecoder[EnumerationTest.WeekDay.Value] = enumDecoder(WeekDay)

  "Enumeration WeekDay" should {
    "encode" in {
      val test: EnumerationTest.WeekDay.Value = WeekDay.Thu
      test.toBson shouldEqual BsonString("Thu")
    }
    "decode" in {
      val bson: BsonValue = BsonString("Thu")
      bson.fromBson[EnumerationTest.WeekDay.Value] shouldEqual Right(WeekDay.Thu)
    }
    "decode unexpected type" in {
      val bson: BsonValue = BsonString("E")
      bson.fromBson[EnumerationTest.WeekDay.Value].isLeft shouldEqual true
    }
  }

  implicit val planetEncoder: BsonEncoder[EnumerationTest.Planet.Value] = enumEncoder(Planet)
  implicit val planetDecoder: BsonDecoder[EnumerationTest.Planet.Value] = enumDecoder(Planet)

  "Enumeration Planet" should {
    "encode" in {
      val test: EnumerationTest.Planet.Value = Planet.Mars
      test.toBson shouldEqual BsonString("Mars")
    }
    "decode" in {
      val bson: BsonValue = BsonString("Mars")
      bson.fromBson[EnumerationTest.Planet.Value] shouldEqual Right(Planet.Mars)
    }
    "decode unexpected type" in {
      val bson: BsonValue = BsonString("E")
      bson.fromBson[EnumerationTest.Planet.Value].isLeft shouldEqual true
    }
  }

}

object EnumerationTest {

  object WeekDay extends Enumeration {
    type WeekDay = Value
    val Mon, Tue, Wed, Thu, Fri, Sat, Sun = Value
  }

  object Planet extends Enumeration {
    protected case class Val(mass: Double, radius: Double) extends super.Val {
      def surfaceGravity: Double = Planet.G * mass / (radius * radius)
      def surfaceWeight(otherMass: Double): Double = otherMass * surfaceGravity
    }
    implicit def valueToPlanetVal(x: Value): Val = x.asInstanceOf[Val]

    val G: Double = 6.67300E-11
    val Mercury = Val(3.303e+23, 2.4397e6)
    val Venus   = Val(4.869e+24, 6.0518e6)
    val Earth   = Val(5.976e+24, 6.37814e6)
    val Mars    = Val(6.421e+23, 3.3972e6)
    val Jupiter = Val(1.9e+27, 7.1492e7)
    val Saturn  = Val(5.688e+26, 6.0268e7)
    val Uranus  = Val(8.686e+25, 2.5559e7)
    val Neptune = Val(1.024e+26, 2.4746e7)
  }

}
