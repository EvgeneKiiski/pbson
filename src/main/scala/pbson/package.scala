import org.mongodb.scala.bson.BsonDocument
import pbson.BsonError._
import pbson.decoder.BsonDecoders
import pbson.encoder._

import scala.util.control.NonFatal

/**
  * @author Evgenii Kiiski 
  */
package object pbson extends BsonEncoders with BsonDecoders {

  implicit class BsonWriterOps[T](val t: T)(implicit encoder: BsonEncoder[T]) {
    def toBson: BsonDocument = encoder(t).asDocument()
  }

  implicit class BsonReaderOps(d: BsonDocument) {
    def fromBson[T]()(implicit decoder: BsonDecoder[T]): Either[BsonError, T] = try {
      decoder(d)
    } catch {
      case NonFatal(e) => Left(WrappedThrowable(e))
    }
  }

}
