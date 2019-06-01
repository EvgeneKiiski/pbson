package pbson.decoder.benchmarks

import org.openjdk.jmh.annotations.{Scope, State}


/**
  * @author Evgenii Kiiski 
  */
@State(Scope.Benchmark)
object Examples {

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

}
