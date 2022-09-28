package com.pain.core.spark

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object BroadcastApp {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()


    broadCastJoin(spark)
    Thread.sleep(40000)

    spark.stop()
  }

  def join(spark: SparkSession): Unit = {
    val basicRdd: RDD[(Int, (Int, String))] = spark.sparkContext.parallelize(Array((1000, "阿甘佐"), (1001, "西岚"), (1002, "巴恩"))).map(x => {
      (x._1, x)
    })

    val detailRdd: RDD[(Int, (Int, String, Int))] = spark.sparkContext.parallelize(Array((1000, "悲鸣洞穴", 34), (1001, "虚祖", 28), (1003, "雪山", 55))).map(x => {
      (x._1, x)
    })

    basicRdd.join(detailRdd).map(x => {
      (x._1, (x._2._1._2, x._2._2._2, x._2._2._3))
    }).foreach(println)
  }

  def broadCastJoin(spark: SparkSession): Unit = {
    val basicMap: collection.Map[Int, String] = spark.sparkContext.parallelize(Array((1000, "阿甘佐"), (1001, "西岚"), (1002, "巴恩"))).collectAsMap()

    val broadcastMap: Broadcast[collection.Map[Int, String]] = spark.sparkContext.broadcast(basicMap)

    val detailRdd: RDD[(Int, (Int, String, Int))] = spark.sparkContext.parallelize(Array((1000, "悲鸣洞穴", 34), (1001, "虚祖", 28), (1003, "雪山", 55))).map(x => {
      (x._1, x)
    })

    detailRdd.mapPartitions(x => {
      val broadcastValue: collection.Map[Int, String] = broadcastMap.value

      for ((key, value) <- x if broadcastValue.contains(key))
        yield (key, broadcastValue.get(key).getOrElse(""), value._2, value._3)
    }).foreach(println)
  }
}
