package com.pain.core.spark.sql

import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Encoders, Row, SparkSession}

object InteroperateApp {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

    // inferSchema(spark)
    specifySchema(spark)

    spark.stop()
  }

  def inferSchema(spark: SparkSession): Unit = {
    import spark.implicits._

    val dataFrame: DataFrame = spark.read.text("/Users/pain/Documents/bigdata/spark/spark-learning/input/people.txt")
    val personDF: DataFrame = dataFrame.map(_.getString(0).split(",")).map(x => Person(x(0), x(1).trim.toLong)).toDF()
    personDF.show()

    personDF.createOrReplaceTempView("people")
    val oldPersonDF: DataFrame = spark.sql("select name, age from people where age > 22")
    oldPersonDF.map(x => "Name: " + x.getAs[String]("name")).show()
  }

  def specifySchema(spark: SparkSession): Unit = {
    import spark.implicits._

    // 方法一
    implicit val mapEncoder = Encoders.kryo[Row]

    val dataFrame: DataFrame = spark.read.text("/Users/pain/Documents/bigdata/spark/spark-learning/input/people.txt")

    // 方法二：dataFrame.map(_.getString(0).split(",")).rdd
    val rowDF: DataFrame = dataFrame.map(_.getString(0).split(",")).map(x => Row(x(0), x(1).trim.toLong)).toDF()
    val structType: StructType = StructType(StructField("name", StringType, nullable = true) :: StructField("age", LongType, nullable = false) :: Nil)
    val df: DataFrame = spark.createDataFrame(rowDF.rdd, structType)

    df.show()
  }

  case class Person(name: String, age: Long)

}
