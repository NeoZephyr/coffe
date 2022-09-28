package com.pain.core.spark.sql

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

object BuildInFuncionApp {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

    val userAccessLog = Array(
      "2016-10-01,1122",
      "2016-10-01,1122",
      "2016-10-01,1123",
      "2016-10-01,1124",
      "2016-10-01,1124",
      "2016-10-02,1122",
      "2016-10-02,1121",
      "2016-10-02,1123",
      "2016-10-02,1123"
    )

    import spark.implicits._

    val rdd: RDD[String] = spark.sparkContext.parallelize(userAccessLog)
    val dataFrame: DataFrame = rdd.map(x => {
      val items: Array[String] = x.split(",")
      Log(items(0), items(1).toInt)
    }).toDF()

    import org.apache.spark.sql.functions._

    dataFrame.groupBy("day").agg(count("userId").as("pv")).show()
    dataFrame.groupBy("day").agg(countDistinct("userId").as("uv")).show()

    spark.stop()
  }

  case class Log(day: String, userId: Int)

}
