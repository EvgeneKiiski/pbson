package pbson.examples

import org.mongodb.scala.bson.{BsonDocument, BsonNull, BsonValue}
import pbson._
import pbson.encoder.DerivedBsonEncoder
import pbson.semiauto._
/**
  * @author Evgenii Kiiski 
  */
object CaseObject extends App {

  case class TestCase(a: Int, b: String)


  implicit val testCaseEncoder: BsonEncoder[TestCase] = deriveEncoder[TestCase]
//    new BsonEncoder[TestCase] {
//    override def apply(t: TestCase): BsonValue = {
//      val aw = implicitly[BsonEncoder[Int]]
//      val bw = implicitly[BsonEncoder[String]]
//      BsonDocument(
//        Map(
//          "a" -> aw(t.a),
//          "b" -> bw(t.b)
//        ).filter {
//          case (_, _: BsonNull) => false
//          case _ => true
//        })
//    }
//  }

  val test = TestCase(3, "45")

  println(BsonWriterOps(test).toBson)
  println(test.toBson)

  //val ttt = implicitly[DerivedBsonEncoder[TestCase]]

}
