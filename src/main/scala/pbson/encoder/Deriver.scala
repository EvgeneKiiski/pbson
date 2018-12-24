package pbson.encoder

import shapeless.{Coproduct, HList}

import scala.reflect.macros.whitebox

/**
  * @author Evgenii Kiiski 
  */
class Deriver(val c: whitebox.Context) {
  import c.universe._

  def deriveEncoder[A: c.WeakTypeTag]: c.Expr[DerivedBsonEncoder[A]] = c.Expr[DerivedBsonEncoder[A]](constructEncoder[A])

  private[this] val HListType: Type = typeOf[HList]
  private[this] val CoproductType: Type = typeOf[Coproduct]

  private[this] def fail(tpe: Type): Nothing =
    c.abort(c.enclosingPosition, s"Cannot generically derive instance: $tpe")

  protected[this] def constructEncoder[A](implicit R: c.WeakTypeTag[A]): c.Tree = {
      q"""
         new DerivedBsonEncoder[TestCase] {
             override def apply(t: TestCase): BsonValue = {
               val aw = implicitly[BsonEncoder[Int]]
               val bw = implicitly[BsonEncoder[String]]
               BsonDocument(
                 Map(
                   "a" -> aw(t.a),
                   "b" -> bw(t.b)
                 ).filter {
                   case (_, _: BsonNull) => false
                   case _ => true
                 })
             }
           }
      """
  }

}
