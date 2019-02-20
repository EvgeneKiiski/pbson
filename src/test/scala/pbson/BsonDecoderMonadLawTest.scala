package pbson

import cats.laws.discipline._
import cats._
import org.mongodb.scala.bson.{ BsonInt32, BsonValue }
import org.scalacheck.{ Arbitrary, Gen }
import org.scalatest.FunSuite
import org.typelevel.discipline.scalatest.Discipline
import pbson.BsonDecoder.Result
import cats.instances.tuple._
import cats.instances.int._
import pbson.gen.AnyBsonGen
//import cats.implicits._


/**
  * @author Evgenii Kiiski 
  */
class BsonDecoderMonadLawTest extends FunSuite with Discipline {

  implicit val monad = new MonadError[BsonDecoder, BsonError] {

    override def flatMap[A, B](fa: BsonDecoder[A])(f: A => BsonDecoder[B]): BsonDecoder[B] =
      fa.flatMap(f)

    override def tailRecM[A, B](a: A)(f: A => BsonDecoder[Either[A, B]]): BsonDecoder[B] = new BsonDecoder[B] {
      import cats.implicits._
      override def apply(b: BsonValue): Result[B] = FlatMap[BsonDecoder.Result].tailRecM(a) { f(_).apply(b) }
    }

    override def raiseError[A](e: BsonError): BsonDecoder[A] = BsonDecoder.raiseError(e)

    override def handleErrorWith[A](fa: BsonDecoder[A])(f: BsonError => BsonDecoder[A]): BsonDecoder[A] =
      fa.handleErrorWith(f)

    override def pure[A](x: A): BsonDecoder[A] = BsonDecoder.pure(x)
  }

  implicit val int2intDecoder: BsonDecoder[Int => Int] = new BsonDecoder[Int => Int] {
    override def apply(b: BsonValue): Result[Int => Int] =
      BsonDecoder[Int].apply(b).map(i => _ => i)
  }

  implicit def arbBsonDecoder[T](implicit a: Arbitrary[T], b: BsonDecoder[T]): Arbitrary[BsonDecoder[T]] =
    Arbitrary(b)

  implicit def eqBsonDecoder[T](implicit t: Eq[T]): Eq[BsonDecoder[T]] = new Eq[BsonDecoder[T]] {
    override def eqv(x: BsonDecoder[T], y: BsonDecoder[T]): Boolean = {
      val bson: BsonValue = AnyBsonGen.anyBsonGen.sample.get
      x(bson) == y(bson)
    }
  }

  implicit val arbBsonError: Arbitrary[BsonError] =
    Arbitrary(AnyBsonGen.anyErrorGen)

  implicit val eqBsonError: Eq[BsonError] = new Eq[BsonError] {
    override def eqv(x: BsonError, y: BsonError): Boolean = x == y
  }

  implicit val eqInt: Eq[Int] = new Eq[Int] {
    override def eqv(x: Int, y: Int): Boolean = x == y
  }

  checkAll("BsonDecoder[Int]", MonadErrorTests[BsonDecoder, BsonError].monad[Int, Int, Int])

}
