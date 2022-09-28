package com.pain.core.spark

import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

object SQLContextApp {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local").setAppName("SQLContextApp")
    val sparkContext: SparkContext = new SparkContext(sparkConf)
    val sqlContext: SQLContext = new SQLContext(sparkContext)

    val dataFrame: DataFrame = sqlContext.read.text("/Users/pain/Documents/bigdata/spark/spark-learning/input/words.txt")
    dataFrame.show(false)

    sparkContext.stop()
  }
}
