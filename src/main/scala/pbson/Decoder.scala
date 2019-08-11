package pbson

/**
  * @author Evgenii Kiiski 
  */
abstract class Decoder[Value, Error, A] { self =>
  def apply(b: Value): Either[Error, A]

  final def map[B](f: A => B): Decoder[Value, Error, B] = b => self(b).map(f)

  final def flatMap[B](f: A => Decoder[Value, Error, B]): Decoder[Value, Error, B] = b =>
    self(b) match {
      case Right(a) => f(a)(b)
      case l @ Left(_) => l.asInstanceOf[Either[Error, B]]
    }

  final def handleErrorWith(f: Error => Decoder[Value, Error, A]): Decoder[Value, Error, A] = b =>
    self(b) match {
      case r @ Right(_) => r.asInstanceOf[Either[Error, A]]
      case Left(e) => f(e)(b)
    }

  final def product[B](db: Decoder[Value, Error, B]): Decoder[Value, Error, (A, B)] = b =>
    self(b) match {
      case Right(a) => db(b) match {
        case Right(rb) => Right((a, rb))
        case l @ Left(_) => l.asInstanceOf[Either[Error, (A, B)]]
      }
      case l @ Left(_) => l.asInstanceOf[Either[Error, (A, B)]]
    }


  final def or[AA >: A](d: => Decoder[Value, Error, AA]): Decoder[Value, Error, AA] = b =>
    self(b) match {
      case r @ Right(_) => r.asInstanceOf[Either[Error, A]]
      case Left(_) => d(b)
    }

  final def mapError[E1](f: Error => E1): Decoder[Value, E1, A] = b => self(b).left.map(f)

}

object Decoder {

  @inline final def apply[V, E, A](implicit d: Decoder[V, E, A]): Decoder[V, E, A] = d

  final def pure[V, E, A](x: A): Decoder[V, E, A] = _ => Right(x)

  final def raiseError[V, E, A](e: E): Decoder[V, E, A] = _ => Left(e)

  final def fromEither[V, E, A](a: Either[E, A]): Decoder[V, E, A] = _ => a
}
