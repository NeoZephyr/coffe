package com.pain.support

import org.apache.commons.codec.binary.Hex
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions.udf

import java.security.MessageDigest
import java.sql.Timestamp
import java.text.SimpleDateFormat

object Functions {

  @transient lazy private val md5 = MessageDigest.getInstance("md5")

  private val dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

  def md5Text(value: String): String = {
    Hex.encodeHexString(md5.digest(value.getBytes("UTF-8")))
  }

  private val arrayToString = (values: Seq[Any]) => {
    val value = values.map(x => x.toString).mkString("---")
    md5Text(value)
  }

  val arrayToStringUDF: UserDefinedFunction = udf(arrayToString)

  def formatValueFunc(format: String) = udf((value: Any) => {
    value match {
      case x: Timestamp =>
        if (x != null) {
          new SimpleDateFormat(format).format(x)
        } else {
          null
        }
      case x =>
        if (x != null) {
          x.toString
        } else {
          null
        }
    }
  })
}
