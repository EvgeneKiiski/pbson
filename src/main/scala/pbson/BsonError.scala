package pbson

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

  final case class WrappedThrowable(th: Throwable) extends BsonError {
    override def toString: String = s"Bson throwable: ${th.getMessage}"
  }

  final case class FieldNotFound(name: String) extends BsonError {
    override def toString: String = s"Bson field $name not found"
  }
}
