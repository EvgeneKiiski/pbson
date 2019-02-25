package pbson.examples

import pbson._
import pbson.semiauto._

/**
  * @author Eugene Kiyski
  * [[https://evgenekiiski.github.io/pbson/]]
  */
object EnumerationExample extends App {

  object WeekDay extends Enumeration {
    type WeekDay = Value
    val Mon, Tue, Wed, Thu, Fri, Sat, Sun = Value
  }

  implicit val weekDayEncoder: BsonEncoder[WeekDay.Value] = enumEncoder(WeekDay)
  implicit val weekDayDecoder: BsonDecoder[WeekDay.Value] = enumDecoder(WeekDay)

  val test : WeekDay.Value = WeekDay.Thu

  val bson = test.toBson
  println(bson)
  println(bson.fromBson[WeekDay.Value])

}
