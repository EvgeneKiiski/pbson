package pbson

import org.bson.BsonType
import org.mongodb.scala.bson.BsonValue

/**
  * @author Evgenii Kiiski 
  */
sealed trait BsonError

object BsonError {

  case object Error extends BsonError {
    override def toString: String = "Bson fatal error"
  }

  final case class UnexpectedType(v: BsonValue, t: BsonType) extends BsonError {
    override def toString: String = s"Bson unexpected type: $v expected: $t"
  }

  final case class UnexpectedValue(s: String) extends BsonError {
    override def toString: String = s"Bson invalid value: $s "
  }


  final case class WrappedThrowable(th: Throwable) extends BsonError {
    override def toString: String = s"Bson throwable: ${th.getMessage}"
  }

  final case class FieldNotFound(name: String) extends BsonError {
    override def toString: String = s"Bson field $name not found"
  }

  final case class ValidateError(s: String) extends BsonError {
    override def toString: String = s"Invalid value $s"
  }

}
