package com.pain.core.spark.rdd

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

object ActionRDDApp {

    def main(args: Array[String]): Unit = {
        val sparkConf: SparkConf = new SparkConf().setAppName("action").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        val rdd: RDD[(String, Int)] = sparkContext.makeRDD(List(("a", 1), ("a", 3), ("c", 3), ("d", 5)))

        // 先聚合分区内数据，再聚合分区间数据
        val tuple: (String, Int) = rdd.reduce((x: (String, Int), y: (String, Int)) => (x._1 + y._1, x._2 + y._2))
        println(tuple)

        // 返回前 n 个元素组成的数组
        println(rdd.take(3).mkString(", "))

        // 返回排序后的前 n 个元素组成的数组
        println(rdd.takeOrdered(3).mkString(", "))

        // testAggregate(sparkContext)
        // testFold(sparkContext)
        testCountByKey(sparkContext)
        // testSaveAsFile(sparkContext)
    }

    /**
     * 通过 seqOp 和初始值进行聚合，通过 combine 将每个分区的结果和初始值进行聚合
     */
    def testAggregate(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 10, 2)
        val aggValue: Int = rdd.aggregate(100)(_ + _, _ + _)
        println(aggValue)
    }

    /**
     * aggregate 的简化操作，seqop 和 combop 一样
     */
    def testFold(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 10, 5)
        val foldValue: Int = rdd.fold(100)(_ + _)
        println(foldValue)
    }

    def testCountByKey(sparkContext: SparkContext): Unit = {
        val rdd: RDD[(Int, Int)] = sparkContext.makeRDD(List((1, 3), (1, 2), (1, 4), (2, 3), (3, 6), (3, 8)), 2)
        val keyToCount: collection.Map[Int, Long] = rdd.countByKey()
        println(keyToCount)
    }

    def testSaveAsFile(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(1 to 10, 2)
        rdd.saveAsTextFile("output/test")
        rdd.saveAsObjectFile("output/object")
    }
}
