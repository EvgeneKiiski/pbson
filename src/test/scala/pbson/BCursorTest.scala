package pbson

import org.bson.BsonType
import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonInt64, BsonString}
import org.scalatest.{EitherValues, Matchers, ParallelTestExecution, WordSpec}
import pbson.BsonError.{ArrayValueNotFound, FieldNotFound, UnexpectedType}

/**
  * @author Evgenii Kiiski 
  */
class BCursorTest extends WordSpec with ParallelTestExecution with Matchers with EitherValues {

  val doc = BsonDocument(
    "a" -> BsonDocument(
      "a" -> BsonInt64(4l),
      "b" -> BsonArray(BsonString("45"), BsonString("23"))
    ),
    "b" -> BsonArray(BsonString("45"), BsonString("23"))
  )

  "BCursor" should {
    "an element" in {
      doc.cursor().down("a").get[Long]("a").right.value shouldEqual 4l
    }
    "not found an element" in {
      doc.cursor().down("c").get[Long]("a").left.value shouldEqual FieldNotFound("c")
    }
    "unsupport type" in {
      BsonInt64(4l).cursor().get[String]("a").left.value shouldEqual UnexpectedType(BsonInt64(4l), BsonType.DOCUMENT)
    }
    "getOrElse" in {
      doc.cursor().down("a").getOrElse[Long]("a")(1l).right.value shouldEqual 4l
    }
    "getOrElse not found" in {
      doc.cursor().down("a").getOrElse[Long]("c")(1l).right.value shouldEqual 1l
    }
    "getOrElse unexpected type" in {
      doc.cursor().down("a").getOrElse[String]("a")("12").left.value shouldEqual UnexpectedType(BsonInt64(4l), BsonType.STRING)
    }
    "find an array  element" in {
      doc.cursor().down("b").find(_ => true).as[String].right.value shouldEqual "45"
    }
    "find an array element but not found" in {
      doc.cursor().down("b").find(_ => false).as[String].left.value shouldEqual ArrayValueNotFound()
    }
    "find an array element but it's not array" in {
      doc.cursor().down("a").down("a").find(_ => true).as[String].left.value shouldEqual UnexpectedType(BsonInt64(4l), BsonType.ARRAY)
    }
  }

}
