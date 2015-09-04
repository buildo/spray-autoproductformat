package spray.json

import scala.language.experimental.macros

trait AutoProductFormattable[T <: Product]

trait AutoProductFormat {
  implicit def jsonFormat[T <: Product](implicit apf: AutoProductFormattable[T]): RootJsonFormat[T] =
      macro AutoProductFormatMacro.autoProductFormatMacro[T]
}

trait LargeProductFormat[A <: Product] {
  def getJsonFormat: RootJsonFormat[A]
}

object AutoProductFormat extends AutoProductFormat

object AutoProductFormatMacro {
  import scala.reflect.macros.blackbox.Context

  def autoProductFormatMacro[T <: Product : c.WeakTypeTag](c: Context)(apf: c.Expr[AutoProductFormattable[T]]): c.Tree = {
    import c.universe._

    val tt = weakTypeTag[T]
    val ts = tt.tpe.typeSymbol.asClass
    val tc = ts.companion

    val args = tt.tpe.decls
      .collect { case m: MethodSymbol if m.isCaseAccessor => m.name -> m.returnType }
      .toList
    val argNames = args.map { case (n, _) => q"${n.toString}" }
    val argTypes = args.map { case (_, t) => tq"$t" }

    if (args.length <= 22) {
      q"_root_.spray.json.DefaultJsonProtocol.jsonFormat[..$argTypes, $ts]($tc.apply, ..$argNames)"
    } else {
      q"implicitly[_root_.spray.json.LargeProductFormat[${tt.tpe}]].getJsonFormat"
    }
  }

}
