package pbson.samples

import java.util.Date

import org.bson.BsonValue
import org.scalatest.{ FlatSpec, Matchers }
import org.mongodb.scala.bson.{ BsonDateTime, BsonDocument, BsonString }
import pbson._
import pbson.semiauto._

/**
  * @author Eugene Kiyski
  */
class GoogleCalendarSubscriptionTest extends FlatSpec with Matchers {

  import GoogleCalendarSubscriptionTest._

  val testsBson: BsonDocument = BsonDocument(
    List(
    "resource" -> BsonString("213"),
    "channel" -> BsonString("chanel"),
    "account" -> BsonString("acc"),
    "extension" -> BsonString("ext"),
    "subscriptionId" -> BsonString("sub"),
    "envName" -> BsonString("env"),
    "expiresAt" -> BsonDateTime(1000),
    "lastTouch" -> BsonDateTime(1000)
    )
  )

  "GoogleCalendarSubscription" should "serialize and deserialize" in {
    val subscription = GoogleCalendarSubscription(
      ResourceId("213"),
      ChannelId("chanel"),
      AccountId("acc"),
      ExtensionId("ext"),
      N11SSubscriptionId("sub"),
      Some(EnvName("env")),
      TimeSeconds(1),
      TimeSeconds(1)
    )
    subscription.toBson shouldEqual testsBson
    println(subscription.toBson)
    println(subscription.toBson.fromBson[GoogleCalendarSubscription])
    subscription.toBson.fromBson[GoogleCalendarSubscription].right.get shouldEqual subscription
  }

}

object GoogleCalendarSubscriptionTest {
  final case class ResourceId(value: String) extends AnyVal
  final case class ChannelId(value: String) extends AnyVal
  final case class AccountId(value: String) extends AnyVal
  final case class ExtensionId(value: String) extends AnyVal
  final case class N11SSubscriptionId(value: String) extends AnyVal
  final case class EnvName(value: String) extends AnyVal
  final case class TimeSeconds(value: Seconds) extends AnyVal {
    def toDate: Date = new Date(value * 1000)
  }
  type Seconds = Long
  object TimeSeconds {
    def apply(value: Date): TimeSeconds = new TimeSeconds(value.getTime / 1000)
  }

  final case class GoogleCalendarSubscription(
    resource: ResourceId,
    channel: ChannelId,
    account: AccountId,
    extension: ExtensionId,
    subscriptionId: N11SSubscriptionId,
    envName: Option[EnvName],
    expiresAt: TimeSeconds,
    lastTouch: TimeSeconds
  )

  sealed trait CISError
  final case class ReadBsonError(error: BsonError) extends CISError

  implicit final val timeSecondsBsonEncoder: BsonEncoder[TimeSeconds] =
    BsonEncoder[java.util.Date].contramap(_.toDate)

  implicit final val timeSecondsBsonDecoder: BsonDecoder[TimeSeconds] =
    BsonDecoder[java.util.Date].map(TimeSeconds(_))
  implicit final val googleCalendarSubscriptionBsonEncoder: BsonEncoder[GoogleCalendarSubscription] =
    deriveEncoder

  implicit final val googleCalendarSubscriptionBsonDecoder: Decoder[BsonValue, CISError, GoogleCalendarSubscription] =
    deriveDecoder[GoogleCalendarSubscription].mapError(ReadBsonError)

}
