import org.mongodb.scala.bson.{BsonDocument, BsonValue}
import pbson.BsonError._

import scala.util.control.NonFatal

/**
  * @author Evgenii Kiiski 
  */
package object pbson {

  implicit class BsonWriterOps[T](val t: T) extends AnyVal {
    final def toBson(implicit encoder: BsonEncoder[T]): BsonDocument = encoder(t).asDocument()
  }

  implicit class BsonReaderOps(val d: BsonDocument) extends AnyVal {
    final def fromBson[T]()(implicit decoder: BsonDecoder[T]): Either[BsonError, T] = try {
      decoder(d)
    } catch {
      case NonFatal(e) => Left(WrappedThrowable(e))
    }
  }

}
