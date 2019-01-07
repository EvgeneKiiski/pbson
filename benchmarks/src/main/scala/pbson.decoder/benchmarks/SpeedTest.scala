package pbson.decoder.benchmarks

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import pbson._
import pbson.examples.FullExample.{NestedCase, TestCase}
import pbson.semiauto._
import cats.syntax.either._
import io.circe._
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._

/**
  * @author Evgenii Kiiski 
  */
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Array(Mode.Throughput, Mode.AverageTime))
@State(Scope.Benchmark)
class SpeedTest {

  implicit val bEncoder: BsonEncoder[Examples.B] = deriveEncoder
  implicit val bDecoder: BsonDecoder[Examples.B] = deriveDecoder
  implicit val aEncoder: BsonEncoder[Examples.A] = deriveEncoder
  implicit val aDecoder: BsonDecoder[Examples.A] = deriveDecoder

  @Benchmark
  def pbsonSmall(): Unit = {
    Examples.small.toBson.fromBson[Examples.A]()
  }

  @Benchmark
  def cicreSmall(): Unit = {
    parse(Examples.small.asJson.toString())
  }

}
