package spray.json

import org.scalatest.{ Matchers, WordSpec }

case class ID[T](t: String)
case class Thing(id: ID[Thing])

class AutoProductFormatSpec {
  // Test for compilation
  implicit object ThingAutoProductFormattable extends AutoProductFormattable[Thing]
  implicit def labOnlineIdFormat[T <: ID[_]] = new JsonFormat[T] {
    def write(id: T) = ???
    def read(idRep: JsValue): T = ???
  }
  AutoProductFormat.jsonFormat[Thing]
}
