
import org.bson.BsonValue
import pbson.utils.{ BsonDecoderUtils, BsonEncoderUtils }


/**
  * @author Evgenii Kiiski 
  */
package object pbson extends BsonDecoderUtils with BsonEncoderUtils {

  implicit class BsonWriterOps[T](val t: T) extends AnyVal {
    final def toBson(implicit encoder: BsonEncoder[T]): BsonValue = encoder(t)
  }

  implicit class BsonReaderOps(val d: BsonValue) extends AnyVal {
    final def fromBson[T]()(implicit decoder: BsonDecoder[T]): Either[BsonError, T] = decoder(d)

    final def cursor(): BCursor = BCursor(Right(d))
  }

}
