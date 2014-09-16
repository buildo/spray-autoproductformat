package spray.json

import scala.language.experimental.macros

trait AutoProductFormattable[T <: Product]

trait AutoProductFormat extends DefaultJsonProtocol {
  implicit def jsonFormat[T <: Product](implicit apf: AutoProductFormattable[T]): RootJsonFormat[T] =
      macro AutoProductFormatMacro.autoProductFormatMacro[T]
}

object AutoProductFormat extends AutoProductFormat


object AutoProductFormatMacro {
  import scala.reflect.macros.Context

  def autoProductFormatMacro[T <: Product : c.WeakTypeTag](c: Context)(apf: c.Expr[AutoProductFormattable[T]]): c.Expr[RootJsonFormat[T]] = {
    import c.universe._

    val tt = weakTypeTag[T]
    val ts = tt.tpe.typeSymbol.asClass

    val args = tt.tpe.declarations
      .collect { case m: MethodSymbol if m.isCaseAccessor => m.name -> m.returnType }
      .toList
    val argNames = args.map { case (n, _) => Literal(Constant(n.toString)) }
    val argTypes = args.map { case (_, t) => parseType(c)(t.toString).head }

    c.Expr[RootJsonFormat[T]](
      Apply(
        TypeApply(
          Select(reify(spray.json.DefaultJsonProtocol).tree, newTermName("jsonFormat")),
          argTypes :+ Ident(ts)
        ),
        Select(Ident(ts.companionSymbol), newTermName("apply")) :: argNames
      )
    )
  }

  protected def parseType(c: Context)(tpe: String): List[c.Tree] = {
    import c.universe._

    def resolveType(qualifiedName: String): Tree =
      qualifiedName.trim().split('.').toList match {
        case x :: Nil => Ident(newTypeName(x))
        case xs :+ x =>
          val parts = xs.map(newTermName(_))
          val pkg: Tree = parts.tail.foldLeft[Tree] (Ident(parts.head)) { (tree, part) => Select(tree, part) }
          Select(pkg, newTypeName(x))
      }

    val x = tpe.indexOf("[")
    if (x > 0) {
      val root = tpe.substring(0, x)
      val parameters = tpe.substring(x + 1, tpe.lastIndexOf("]"))
      List(
        AppliedTypeTree(
          resolveType(root),
          parseType(c)(parameters)
        )
      )
    } else {
      tpe.split(',').toList.map(resolveType(_))
    }
  }

}
