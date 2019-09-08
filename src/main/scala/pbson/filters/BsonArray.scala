package pbson.filters

import org.bson.BsonValue
import collection.JavaConverters._

/**
  * @author Evgenii Kiiski 
  */
object BsonArray {

  def apply(a: BsonValue): BsonArray = {
    val array = new BsonArray()
    array.add(a)
    array
  }

  def apply(a: BsonValue, b: BsonValue): BsonArray = {
    val array = new BsonArray()
    array.add(a)
    array.add(b)
    array
  }

  def apply(a: BsonValue, b: BsonValue, c: BsonValue): BsonArray = {
    val array = new BsonArray()
    array.add(a)
    array.add(b)
    array.add(c)
    array
  }

  def apply(a: BsonValue, b: BsonValue, c: BsonValue, d: BsonValue): BsonArray = {
    val array = new BsonArray()
    array.add(a)
    array.add(b)
    array.add(c)
    array.add(d)
    array
  }

  def apply(a: BsonValue, b: BsonValue, c: BsonValue, d: BsonValue, e: BsonValue): BsonArray = {
    val array = new BsonArray()
    array.add(a)
    array.add(b)
    array.add(c)
    array.add(d)
    array.add(e)
    array
  }

  def apply(
    a: BsonValue,
    b: BsonValue,
    c: BsonValue,
    d: BsonValue,
    e: BsonValue,
    f: BsonValue
  ): BsonArray = {
    val array = new BsonArray()
    array.add(a)
    array.add(b)
    array.add(c)
    array.add(d)
    array.add(e)
    array.add(f)
    array
  }

  def apply(
    a: BsonValue,
    b: BsonValue,
    c: BsonValue,
    d: BsonValue,
    e: BsonValue,
    f: BsonValue,
    g: BsonValue
  ): BsonArray = {
    val array = new BsonArray()
    array.add(a)
    array.add(b)
    array.add(c)
    array.add(d)
    array.add(e)
    array.add(f)
    array.add(g)
    array
  }

  def apply(
    a: BsonValue,
    b: BsonValue,
    c: BsonValue,
    d: BsonValue,
    e: BsonValue,
    f: BsonValue,
    g: BsonValue,
    h: BsonValue
  ): BsonArray = {
    val array = new BsonArray()
    array.add(a)
    array.add(b)
    array.add(c)
    array.add(d)
    array.add(e)
    array.add(f)
    array.add(g)
    array.add(h)
    array
  }

  def apply(as: Seq[BsonValue]): BsonArray = {
    val array = new BsonArray()
    array.addAll(as.asJava)
    array
  }

}
