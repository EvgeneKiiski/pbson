package pbson.utils

import shapeless.{ ::, HNil }

/**
  * @author Evgenii Kiiski 
  */
trait AnyValUtils {

  sealed trait AnyValHelper[Repr] extends Serializable {
    type U
    def unwrap(r: Repr): U
    def wrap(u: U): Repr
  }
  object AnyValHelper {
    type Aux[Repr, U0] = AnyValHelper[Repr] {type U = U0}

    implicit def sizeOneHListHelper[T] =
      SizeOneHListHelper.asInstanceOf[AnyValHelper.Aux[T :: HNil, T]]

    val SizeOneHListHelper = new AnyValHelper[Any :: HNil] {
      type U = Any

      def unwrap(hl: Any :: HNil): Any = hl.head

      def wrap(t: Any): Any :: HNil = t :: HNil
    }
  }

}
