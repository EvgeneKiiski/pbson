package pbson

import org.mongodb.scala.bson.BsonValue

/**
  * @author Evgenii Kiiski 
  */
sealed trait BsonError

object BsonError {

  case object Error extends BsonError {
    override def toString: String = "Bson fatal error"
  }

  final case class InvalidType(s: String) extends BsonError {
    override def toString: String = s"Bson invalid type: $s"
  }

  object InvalidType {
    def apply(b: BsonValue): InvalidType =
      if (b != null) {
        new InvalidType(b.toString)
      } else {
        new InvalidType("null")
      }
  }

  final case class WrappedThrowable(th: Throwable) extends BsonError {
    override def toString: String = s"Bson throwable: ${th.getMessage}"
  }

  final case class FieldNotFound(name: String) extends BsonError {
    override def toString: String = s"Bson field $name not found"
  }

  final case object BsonIsNull extends BsonError {
    override def toString: String = s"BsonValue is null"
  }

  final case class ValidateError(s: String) extends BsonError {
    override def toString: String = s"Invalid value $s"
  }

}
