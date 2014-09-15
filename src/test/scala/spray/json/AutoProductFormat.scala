package spray.json

import org.scalatest.{ Matchers, WordSpec }

class AutoProductFormatSpec extends WordSpec with Matchers {
  "implicit jsonFormat" should {
    "work with higher kinded types" in {
      case class ID[T](t: T)
      implicit def labOnlineIdFormat[T <: ID[_]] = new JsonFormat[T] {
        def write(id: T) = ???
        def read(idRep: JsValue): T = ???
      }
      case class Thing(id: ID[Thing])
      implicit object ThingAutoProductFormattable extends AutoProductFormattable[Thing]
      AutoProductFormat.jsonFormat[Thing]
    }
  }
}
