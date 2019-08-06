package pbson

/**
  * @author Evgenii Kiiski 
  */
abstract class Encoder[A] { self =>
  type Value

  def apply(t: A): Value

//  @inline final def contramap[B](f: B => A): Encoder.Aux[B, Value] = new Encoder[B] {
//    final type Value = self.Value
//
//    override def apply(b: B): Value = self(f(b))
//  }

}

object Encoder {

  type Aux[A, Value0] = Encoder[A]{ type Value = Value0 }

  @inline final def apply[V, A](implicit e: Encoder.Aux[A, V]): Encoder[A] = e

}
