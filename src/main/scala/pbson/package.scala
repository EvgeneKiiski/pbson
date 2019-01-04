import org.mongodb.scala.bson.BsonDocument
import pbson.BsonError._
import scala.util.control.NonFatal

/**
  * @author Evgenii Kiiski 
  */
package object pbson {

  implicit class BsonWriterOps[T](val t: T) extends AnyVal {
    def toBson(implicit encoder: BsonEncoder[T]): BsonDocument = encoder(t).asDocument()
  }

  implicit class BsonReaderOps(val d: BsonDocument) extends AnyVal {
    def fromBson[T]()(implicit decoder: BsonDecoder[T]): Either[BsonError, T] = try {
      decoder(d)
    } catch {
      case NonFatal(e) => Left(WrappedThrowable(e))
    }
  }

}
