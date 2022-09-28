package com.pain.support

import java.util.{Map => JavaMap}

object JavaMapOps {

  implicit class JavaMapValue(val javaMap: JavaMap[String, Any]) extends AnyVal {
    def getV[T](key: String): T = {
      javaMap.get(key) match {
        case value: T => value
      }
    }

    def getV[T](key: String, defaultValue: T): T = {
      javaMap.get(key) match {
        case value: T => value
        case _ => defaultValue
      }
    }

    def getOption[T](key: String): Option[T] = {
      javaMap.get(key) match {
        case value: T => Some(value)
        case _ => None
      }
    }
  }

}
