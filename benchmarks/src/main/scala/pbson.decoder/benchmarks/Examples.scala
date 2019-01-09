package pbson.decoder.benchmarks

import org.bson.codecs.{Codec, DecoderContext, EncoderContext}
import org.mongodb.scala.bson.codecs.Macros
import org.openjdk.jmh.annotations.{Scope, State}



/**
  * @author Evgenii Kiiski 
  */
@State(Scope.Benchmark)
object Examples {

  case class A(
                int: Int,
                str: Option[String],
                map: Map[String, Int],
                seq: Seq[B]
              )

  case class B(
                id: Long,
                name: String
              )


  val small = A(
    3,
    Some("str"),
    Map("str" -> 5, "s" -> 4),
    Seq(B(23, "1"), B(24, "5"))
  )

  import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
  val mongoProvider = Macros.createCodecProvider[Examples.A]()
  val codec  = mongoProvider.get(Examples.small.getClass, DEFAULT_CODEC_REGISTRY)
  val contextE = EncoderContext.builder().build()
  val contextD = DecoderContext.builder().build()

}
