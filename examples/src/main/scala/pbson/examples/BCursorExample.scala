package pbson.examples

import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonInt64, BsonString}
import pbson.BsonDecoder.Result
import pbson._


/**
  * @author Evgenii Kiiski
  * [[https://evgenekiiski.github.io/pbson/]]
  */
object BCursorExample extends App {

  val doc = BsonDocument(
    "a" -> BsonDocument(
      "a" -> BsonInt64(4l),
      "b" -> BsonArray(BsonString("45"), BsonString("23"))
    ),
    "b" -> BsonArray(BsonString("45"), BsonString("23"))
  )

  val av = doc.cursor().down("a").get[Long]("a")
  println(av)
  val av1 = doc.cursor().down("c").get[Long]("a")
  println(av1)

}
