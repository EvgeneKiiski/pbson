import org.mongodb.scala.bson.{BsonDocument, BsonValue}
import pbson.BsonError._
import pbson.utils.{BsonDecoderUtils, BsonEncoderUtils}

import scala.util.control.NonFatal

/**
  * @author Evgenii Kiiski 
  */
package object pbson extends BsonDecoderUtils with BsonEncoderUtils {

  implicit class BsonWriterOps[T](val t: T) extends AnyVal {
    final def toBson(implicit encoder: BsonEncoder[T]): BsonValue = encoder(t)
  }

  implicit class BsonReaderOps(val d: BsonValue) extends AnyVal {
    final def fromBson[T]()(implicit decoder: BsonDecoder[T]): Either[BsonError, T] = try {
      decoder(d)
    } catch {
      case NonFatal(e) => Left(WrappedThrowable(e))
    }
  }

}
