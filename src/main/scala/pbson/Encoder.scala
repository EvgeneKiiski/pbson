package pbson

/**
  * @author Evgenii Kiiski 
  */
abstract class Encoder[Value, A] { self =>

  def apply(t: A): Value

  @inline final def contramap[B](f: B => A): Encoder[Value, B] = b => self(f(b))
}

object Encoder {

  @inline final def apply[V, A](implicit e: Encoder[V, A]): Encoder[V, A] = e
}
