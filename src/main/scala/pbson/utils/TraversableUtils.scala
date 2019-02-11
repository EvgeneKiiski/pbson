package pbson.utils

import scala.util.control.ControlThrowable

/**
  * @author Eugene Kiyski
  */
object TraversableUtils {

  private final case class BreakLeft[E](e: E) extends ControlThrowable

  final def traverse2Seq[A, B, E](ta: Traversable[A])(f: A => Either[E, B]): Either[E, Seq[B]] = {
    val builder = Seq.newBuilder[B]
    builder.sizeHint(ta)
    try {
      ta.foreach { a =>
        f(a) match {
          case Right(v) => builder += v
          case Left(e) => throw BreakLeft(e)
        }
      }
      Right(builder.result())
    } catch {
      case BreakLeft(e) => Left(e.asInstanceOf[E])
    }
  }

  final def traverse2List[A, B, E](ta: Traversable[A])(f: A => Either[E, B]): Either[E, List[B]] = {
    val builder = List.newBuilder[B]
    builder.sizeHint(ta)
    try {
      ta.foreach { a =>
        f(a) match {
          case Right(v) => builder += v
          case Left(e) => throw BreakLeft(e)
        }
      }
      Right(builder.result())
    } catch {
      case BreakLeft(e) => Left(e.asInstanceOf[E])
    }
  }

  final def traverse2Set[A, B, E](ta: Traversable[A])(f: A => Either[E, B]): Either[E, Set[B]] = {
    val builder = Set.newBuilder[B]
    builder.sizeHint(ta)
    try {
      ta.foreach { a =>
        f(a) match {
          case Right(v) => builder += v
          case Left(e) => throw BreakLeft(e)
        }
      }
      Right(builder.result())
    } catch {
      case BreakLeft(e) => Left(e.asInstanceOf[E])
    }
  }

  final def traverse2Vector[A, B, E](ta: Traversable[A])(f: A => Either[E, B]): Either[E, Vector[B]] = {
    val builder = Vector.newBuilder[B]
    builder.sizeHint(ta)
    try {
      ta.foreach { a =>
        f(a) match {
          case Right(v) => builder += v
          case Left(e) => throw BreakLeft(e)
        }
      }
      Right(builder.result())
    } catch {
      case BreakLeft(e) => Left(e.asInstanceOf[E])
    }
  }

  final def traverse2Map[A, K, V, E](ta: Traversable[A])(f: A => Either[E, (K, V)]): Either[E, Map[K, V]] = {
    val builder = Map.newBuilder[K, V]
    builder.sizeHint(ta)
    try {
      ta.foreach { a =>
        f(a) match {
          case Right(v) => builder += v
          case Left(e) => throw BreakLeft(e)
        }
      }
      Right(builder.result())
    } catch {
      case BreakLeft(e) => Left(e.asInstanceOf[E])
    }
  }

}
