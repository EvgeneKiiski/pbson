package pbson

/**
  * @author Evgenii Kiiski 
  */
abstract class Decoder[A] { self =>
  type Value
  type Error
  def apply(b: Value): Either[Error, A]

//  final def map[B](f: A => B): Decoder.Aux[B, Value, Error] = new Decoder[B] {
//    final type Value = self.Value
//    final type Error = self.Error
//
//    final def apply(b: Value): Either[Error, B] = self(b).map(f)
//  }
//
//  final def flatMap[B, DB, V <: Value, E <: Error](f: A => DB)(implicit
//    ev: DB =:= Decoder.Aux[B, V, E]
//  ): Decoder.Aux[B, V, E] = new Decoder[B] {
//    final type Value = V
//    final type Error = E
//
//    final def apply(b: Value): Either[Error, B] = self(b) match {
//      case Right(a) => f(a)(b)
//      case l@Left(_) => l.asInstanceOf[Either[Error, B]]
//    }
//  }
//
//  final def handleErrorWith[DA, V <: Value, E <: Error](f: Error => DA)(implicit
//    ev: DA =:= Decoder.Aux[A, V, E]
//  ): Decoder.Aux[A, V, E] = new Decoder[A] {
//    final type Value = V
//    final type Error = E
//
//    final def apply(b: Value): Either[Error, A] = self(b) match {
//      case r@Right(_) => r.asInstanceOf[Either[Error, A]]
//      case Left(e) => f(e)(b)
//    }
//  }
//
//  final def product[B, DB, V <: Value, E <: Error](db: DB)(implicit
//    ev: DB =:= Decoder.Aux[B, V, E]
//  ): Decoder.Aux[(A, B), V, E] = new Decoder[(A, B)] {
//    final type Value = V
//    final type Error = E
//
//    final def apply(b: Value): Either[Error, (A, B)] = self(b) match {
//      case Right(a) => db(b) match {
//        case Right(rb) => Right((a, rb))
//        case l@Left(_) => l.asInstanceOf[Either[Error, (A, B)]]
//      }
//      case l@Left(_) => l.asInstanceOf[Either[Error, (A, B)]]
//    }
//  }
//
//
//  final def or[AA >: A, DAA, V <: Value, E <: Error](d: => DAA)(implicit
//    ev: DAA =:= Decoder.Aux[A, V, E]
//  ): Decoder.Aux[AA, V, E] = new Decoder[AA] {
//    final type Value = V
//    final type Error = E
//
//    final def apply(b: Value): Either[Error, AA] = self(b) match {
//      case r@Right(_) => r.asInstanceOf[Either[Error, A]]
//      case Left(_) => d(b)
//    }
//  }

  final def mapError[E1](f: Error => E1): Decoder.Aux[A, Value, E1] = new Decoder[A] {
    final type Value = self.Value
    final type Error = E1

    final def apply(b: Value): Either[E1, A] = self(b).left.map(f)
  }

}

object Decoder {

  type Aux[A, Value0, Error0] = Decoder[A]{
    type Value = Value0
    type Error = Error0
  }

  @inline final def apply[V, E, A](implicit d: Decoder.Aux[A, V, E]): Decoder[A] = d

  final def pure[V, E, A](x: A): Decoder[A] = new Decoder[A] {
    final type Value = V
    final type Error = E

    override def apply(b: V): Either[E, A] = Right(x)
  }

  final def raiseError[V, E, A](e: E): Decoder[A] = new Decoder[A] {
    final type Value = V
    final type Error = E

    final def apply(b: V): Either[E, A] = Left(e)
  }

  final def fromEither[V, E, A](a: Either[E, A]): Decoder[A] = new Decoder[A] {
    final type Value = V
    final type Error = E

    final def apply(b: V): Either[E, A] = a
  }
}
