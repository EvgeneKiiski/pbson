package pbson.decoder.benchmarks

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import pbson._
import pbson.examples.CaseClass2Example.{NestedCase, TestCase}
import pbson.semiauto._
import cats.syntax.either._
import io.circe._
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._
import org.bson._
import org.bson.codecs.{Codec, DecoderContext, EncoderContext}
import org.bson.types.{Decimal128, ObjectId}
import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonInt64, BsonString}
import org.mongodb.scala.bson.codecs.Macros
import pbson.decoder.benchmarks.Examples._

import scala.collection.JavaConverters._

/**
  * @author Evgenii Kiiski 
  */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Array(Mode.Throughput))
@State(Scope.Benchmark)
class SpeedTest {

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

  implicit val bEncoder: BsonEncoder[Examples.B] = deriveEncoder
  implicit val bDecoder: BsonDecoder[Examples.B] = deriveDecoder
  implicit val aEncoder: BsonEncoder[Examples.A] = deriveEncoder
  implicit val aDecoder: BsonDecoder[Examples.A] = deriveDecoder


  @Benchmark
  def cicreSmall(): Either[ParsingFailure, Json] = {
    parse(Examples.small.asJson.toString())
  }

  @Benchmark
  def manualSmall(): A = {
    val bson = BsonDocument(
      "int" -> 3,
      "str" -> "str",
      "map" -> BsonDocument("str" -> 5, "s" -> 4),
      "seq" -> BsonArray(Seq(
        BsonDocument("id"-> BsonInt64(23), "name" -> BsonString("1")),
        BsonDocument("id"-> BsonInt64(24), "name" -> BsonString("5"))
      )),
      "adt" -> BsonString("B")
    )

    val doc = bson.asDocument()
    val int = doc.get("int").asInt32().getValue
    val str = if (doc.containsKey("str")) Some(doc.get("str").asString().getValue) else None
    val map = doc.get("map").asDocument().asScala.map { case (k, v) => (k, v.asInt32().getValue)}.toMap
    val sq = doc.get("seq").asArray().asScala
      .map(_.asDocument()).map(d => B(d.get("id").asInt64().getValue, d.get("name").asString().getValue))
    val adt = doc.get("adt").asString().getValue match {
      case "A" => ADT1()
      case "B" => ADT2()
      case "C" => ADT3()
    }
    val result = A(int, str, map, sq, adt)
    result
  }

  @Benchmark
  def pbsonSmall(): Either[BsonError, A] = {
    Examples.small.toBson.fromBson[Examples.A]()
  }

}
