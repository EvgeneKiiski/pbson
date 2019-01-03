package pbson.examples

import pbson._
import pbson.semiauto._

/**
  * @author Evgenii Kiiski 
  */
object FullExample extends App {

  sealed trait SealedTest

  object SealedTest {
    final case class Two(s: String) extends SealedTest
    final case class Three(i: Int) extends SealedTest

  }

  case class TestCase(a: Int, b: Option[String], st: SealedTest) extends Product

  import SealedTest._

  implicit val twoEncoder: BsonEncoder[Two] = deriveEncoder
  implicit val threeEncoder: BsonEncoder[Three] = deriveEncoder
  implicit val sealedEncoder: BsonEncoder[SealedTest] = deriveEncoder
  implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder


  val test = TestCase(3, Some("45"), Two("99"))

  println(BsonWriterOps(test).toBson)
  println(test.toBson)

  //val ttt = implicitly[DerivedBsonEncoder[TestCase]]

}
