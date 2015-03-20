package spray.json

import scala.language.experimental.macros

trait AutoProductFormattable[T <: Product]

trait AutoProductFormat {
  implicit def jsonFormat[T <: Product](implicit apf: AutoProductFormattable[T]): RootJsonFormat[T] =
      macro AutoProductFormatMacro.autoProductFormatMacro[T]
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

    q"_root_.spray.json.DefaultJsonProtocol.jsonFormat[..$argTypes, $ts]($tc.apply, ..$argNames)"
  }

  protected def parseType(c: Context)(tpe: String): List[c.Tree] = {
    import c.universe._

    def resolveType(qualifiedName: String): Tree =
      qualifiedName.trim().split('.').toList match {
        case x :: Nil => q"$x"
        case xs => tq"""${xs.mkString(".")}"""
      }

    val x = tpe.indexOf("[")
    if (x > 0) {
      val root = tpe.substring(0, x)
      val parameters = tpe.substring(x + 1, tpe.lastIndexOf("]"))
      val rt = resolveType(root)
      val pts = parseType(c)(parameters)
      List(tq"$rt[..$pts]")
    } else {
      tpe.split(',').toList.map(resolveType(_))
    }
  }

}
