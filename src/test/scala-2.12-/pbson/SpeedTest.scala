package pbson

import org.mongodb.scala.bson.{ BsonArray, BsonDocument, BsonInt32, BsonInt64, BsonString }
import org.scalatest.{ Matchers, ParallelTestExecution, WordSpec }
import pbson.semiauto._

import scala.collection.JavaConverters._

/**
  * @author Evgenii Kiiski 
  */
class SpeedTest extends WordSpec with ParallelTestExecution with Matchers {

  sealed trait ADT

  case class ADT1() extends ADT

  case class ADT2() extends ADT

  case class ADT3() extends ADT

  case class A(
                int: Int,
                str: Option[String],
                map: Map[String, Int],
                seq: Seq[B],
                adt: ADT
              )

  case class B(
                id: Long,
                name: String
              )


  val small = A(
    3,
    Some("str"),
    Map("str" -> 5, "s" -> 4),
    Seq(B(23, "1"), B(24, "5")),
    ADT2()
  )

  implicit val adtEnumEncoder: BsonEncoder[ADT] = asStringEncoder {
    case ADT1() => "A"
    case ADT2() => "B"
    case ADT3() => "C"
  }

  implicit val adtEnumDecoder: BsonDecoder[ADT] = asStringDecoder {
    case "A" => ADT1()
    case "B" => ADT2()
    case "C" => ADT3()
  }

  implicit val aEncoder: BsonEncoder[A] = deriveEncoder
  implicit val aDecoder: BsonDecoder[A] = deriveDecoder

  val bson = BsonDocument(
    "int" -> BsonInt32(3),
    "str" -> BsonString("str"),
    "map" -> BsonDocument("str" -> BsonInt32(5), "s" -> BsonInt32(4)),
    "seq" -> BsonArray(Seq(
      BsonDocument("id"-> BsonInt64(23), "name" -> BsonString("1")),
      BsonDocument("id"-> BsonInt64(24), "name" -> BsonString("5"))
    )),
    "adt" -> BsonString("B")
  )

  "SpeedTest" should {
    "encode" in {
      val bson = small.toBson
      println(bson)
    }
    "decode" in {
      bson.fromBson[A] shouldEqual Right(small)
    }
    "manual decode" in {
      val doc = bson.asDocument()
      val int = doc.get("int").asInt32().getValue
      val str = if (doc.containsKey("str")) Some(doc.get("str").asString().getValue) else None
      val map = doc.get("map").asDocument().asScala.map { case (k, v) => (k, v.asInt32().getValue)}.toMap
      val sq = doc.get("seq").asArray().asScala
        .map(_.asDocument()).map(d => B(d.get("id").asInt64().getValue, d.get("name").asString().getValue)).toSeq
      val adt = doc.get("adt").asString().getValue match {
        case "A" => ADT1()
        case "B" => ADT2()
        case "C" => ADT3()
      }
      val result = A(int, str, map, sq, adt)
      result shouldEqual small
    }
  }

}
