package com.heqiying.fundmng.settle.model

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

abstract class Model[T: TypeTag: ClassTag] {
  override def toString() = {
    val mirror = runtimeMirror(getClass.getClassLoader).reflect(this)
    val args = typeOf[T].members.collect {
      case m: MethodSymbol if m.isCaseAccessor =>
        val name = m.name.toString
        val value = mirror.reflectMethod(m).apply()
        (name, value)
    }
    val argValues = args.toSeq.sortBy(_._1).map { case (k, v) => s"$k($v)" }.mkString("; ")
    val className = implicitly[ClassTag[T]].toString()
    s"""${className.slice(className.lastIndexOf('.') + 1, className.length)} { $argValues }"""
  }
}
