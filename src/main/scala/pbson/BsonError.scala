package pbson

/**
  * @author Evgenii Kiiski 
  */
sealed trait BsonError

object BsonError {
  case object Error extends BsonError

  final case class InvalidType(str: String) extends BsonError

  final case class WrappedThrowable(th: Throwable) extends BsonError
}
