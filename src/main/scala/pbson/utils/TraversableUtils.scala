package pbson.utils

import java.util

import org.mongodb.scala.bson.BsonValue

import scala.util.control.ControlThrowable

/**
  * @author Eugene Kiyski
  */
object TraversableUtils {

  private final case class BreakLeft[E](e: E) extends ControlThrowable

  final def traverse2Seq[A, B, E](ta: util.Collection[A])(f: A => Either[E, B]): Either[E, Seq[B]] = {
    val builder = Seq.newBuilder[B]
    builder.sizeHint(ta.size())
    try {
      val iterator = ta.iterator()
      while (iterator.hasNext) {
        f(iterator.next()) match {
          case Right(v) => builder += v
          case Left(e) => throw BreakLeft(e)
        }
      }
      Right(builder.result())
    } catch {
      case BreakLeft(e) => Left(e.asInstanceOf[E])
    }
  }

  final def traverse2List[A, B, E](ta: util.Collection[A])(f: A => Either[E, B]): Either[E, List[B]] = {
    val builder = List.newBuilder[B]
    builder.sizeHint(ta.size())
    try {
      val iterator = ta.iterator()
      while (iterator.hasNext) {
        f(iterator.next()) match {
          case Right(v) => builder += v
          case Left(e) => throw BreakLeft(e)
        }
      }
      Right(builder.result())
    } catch {
      case BreakLeft(e) => Left(e.asInstanceOf[E])
    }
  }

  final def traverse2Set[A, B, E](ta: util.Collection[A])(f: A => Either[E, B]): Either[E, Set[B]] = {
    val builder = Set.newBuilder[B]
    builder.sizeHint(ta.size())
    try {
      val iterator = ta.iterator()
      while (iterator.hasNext) {
        f(iterator.next()) match {
          case Right(v) => builder += v
          case Left(e) => throw BreakLeft(e)
        }
      }
      Right(builder.result())
    } catch {
      case BreakLeft(e) => Left(e.asInstanceOf[E])
    }
  }

  final def traverse2Vector[A, B, E](ta: util.Collection[A])(f: A => Either[E, B]): Either[E, Vector[B]] = {
    val builder = Vector.newBuilder[B]
    builder.sizeHint(ta.size())
    try {
      val iterator = ta.iterator()
      while (iterator.hasNext) {
        f(iterator.next()) match {
          case Right(v) => builder += v
          case Left(e) => throw BreakLeft(e)
        }
      }
      Right(builder.result())
    } catch {
      case BreakLeft(e) => Left(e.asInstanceOf[E])
    }
  }

  final def traverse2Map[IK, IV, K, V, E](ta: util.Map[IK, IV])(f: ((IK, IV)) => Either[E, (K, V)]): Either[E, Map[K, V]] = {
    val builder = Map.newBuilder[K, V]
    builder.sizeHint(ta.size())
    try {
      val iterator: util.Iterator[util.Map.Entry[IK, IV]] = ta.entrySet().iterator()
      while (iterator.hasNext) {
        val value: util.Map.Entry[IK, IV] = iterator.next()
        f((value.getKey, value.getValue)) match {
          case Right(v) => builder += v
          case Left(e) => throw BreakLeft(e)
        }
      }
      Right(builder.result())
    } catch {
      case BreakLeft(e) => Left(e.asInstanceOf[E])
    }
  }

  final def traverse2Map[A, K, V, E](ta: util.Collection[A])(f: A => Either[E, (K, V)]): Either[E, Map[K, V]] = {
    val builder = Map.newBuilder[K, V]
    builder.sizeHint(ta.size())
    try {
      val iterator = ta.iterator()
      while (iterator.hasNext) {
        f(iterator.next()) match {
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
