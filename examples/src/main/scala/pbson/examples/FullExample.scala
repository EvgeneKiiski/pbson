package pbson.examples

import pbson._
import pbson.examples.SimpleExample.{TestCase, test}
import pbson.semiauto._

/**
  * @author Evgenii Kiiski 
  */
object FullExample extends App {

  sealed trait SealedTest

  object SealedTest {
    final case class One() extends SealedTest
    final case class Two(s: String) extends SealedTest
    final case class Three(i: Int) extends SealedTest

  }

  import SealedTest._

  implicit val oneEncoder: BsonEncoder[One] = deriveEncoder
  implicit val twoEncoder: BsonEncoder[Two] = deriveEncoder
  implicit val threeEncoder: BsonEncoder[Three] = deriveEncoder
  implicit val sealedEncoder: BsonEncoder[SealedTest] = deriveEncoder

  case class TestCase(
                       a: Int,
                       b: Option[String],
                       c: Long,
                       d: Seq[Long]
                       //st: SealedTest
                     ) extends Product


  implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder
  implicit val testCaseDecoder: BsonDecoder[TestCase] = deriveDecoder


  val test = TestCase(
    3,
    Some("45"),
    34l,
    List(2l, 5l)
  )

  val bson = test.toBson
  println(bson)
  println(bson.fromBson[TestCase])

}
