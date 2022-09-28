package com.pain.core.spark.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

object TransformRDDApp {

    def main(args: Array[String]): Unit = {
        val sparkConf: SparkConf = new SparkConf().setAppName("transformApp").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)
        val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()
        val rdd: RDD[String] = spark.sparkContext.textFile("input/words.txt")

        // mapRdd(rdd)
        // filterRdd(rdd)
        // groupByRdd(rdd)
        // sortByRdd(rdd)
        // groupByKeyRdd(rdd)
        partitionRdd(rdd)
        // reduceByKeyRdd(rdd)
        // sortByKeyRdd(rdd)
        // setOptRdd(sparkContext)
        // cogroupRdd(sparkContext)
        // joinRdd(sparkContext)

        // sampleRdd(sparkContext)
    }

    def mapRdd(rdd: RDD[String]): Unit = {
        // 每次处理一条数据
        val mapRdd: RDD[Array[String]] = rdd.map((_: String).split(" "))
        mapRdd.collect().foreach((arr: Array[String]) => println(arr.mkString("|")))
    }

    def filterRdd(rdd: RDD[String]): Unit = {
        val flatMapRdd: RDD[String] = rdd.flatMap((_: String).split(" "))
        val filterRdd: RDD[String] = flatMapRdd.filter((_: String).length % 3 == 0)
        filterRdd.collect().foreach(println(_: String))
    }

    def groupByRdd(rdd: RDD[String]): Unit = {
        val flatMapRdd: RDD[String] = rdd.flatMap((_: String).split(" "))
        val groupByRdd: RDD[(Int, Iterable[String])] = flatMapRdd.groupBy((_: String).length)
        groupByRdd.foreach((item: (Int, Iterable[String])) =>
            println(s"key: ${item._1}, value: ${item._2.mkString(", ")}"))
    }

    def sortByRdd(rdd: RDD[String]): Unit = {
        val flatMapRdd: RDD[String] = rdd.flatMap((_: String).split(" ")).coalesce(1)
        val sortRdd: RDD[String] = flatMapRdd.sortBy((x: String) => x.length, ascending = false)
        sortRdd.foreach(println(_: String))
    }

    def partitionRdd(rdd: RDD[String]): Unit = {
        println(s"partition number: ${rdd.partitions.length}")
        rdd.glom().collect().foreach((x: Array[String]) => println(x.mkString(", ")))

        val repartitionRdd: RDD[String] = rdd.repartition(3)
        println(s"partition number: ${repartitionRdd.partitions.length}")
        repartitionRdd.glom().collect().foreach((x: Array[String]) => println(x.mkString(", ")))

        val coalesceRdd: RDD[String] = rdd.coalesce(1)
        println(s"partition number: ${coalesceRdd.partitions.length}")
        coalesceRdd.glom().collect().foreach((x: Array[String]) => println(x.mkString(", ")))

        val mapPartitionRdd: RDD[(Int, String)] = repartitionRdd.mapPartitionsWithIndex((index: Int, item: Iterator[String]) => {
            item.map((index, (_: String)))
        })
        mapPartitionRdd.collect().foreach(println)
    }

    def groupByKeyRdd(rdd: RDD[String]): Unit = {
        val mapRdd: RDD[(String, Int)] = rdd
            .repartition(3)
            .flatMap((_: String).split(" "))
            .map(((_: String), 1))
        println(mapRdd.partitions.length)
        val groupByKeyRdd: RDD[(String, Iterable[Int])] = mapRdd.groupByKey()
        println(groupByKeyRdd.partitions.length)
        groupByKeyRdd
            .collect()
            .foreach(
                (item: (String, Iterable[Int])) =>
                    println(s"key: ${item._1}, value: ${item._2.mkString(", ")}"))
    }

    def reduceByKeyRdd(rdd: RDD[String]): Unit = {
        val mapRdd: RDD[(String, Int)] = rdd
            .flatMap((_: String).split(" "))
            .map(((_: String), 1))
        val reduceByKeyRdd: RDD[(String, Int)] = mapRdd.reduceByKey(_ + _)
        reduceByKeyRdd.collect().foreach(println)
    }

    def sortByKeyRdd(rdd: RDD[String]): Unit = {
        val mapRdd: RDD[(Int, String)] = rdd
            .flatMap((_: String).split(" "))
            .map(((_: String), 1))
            .reduceByKey((_: Int) + (_: Int))
            .map((x: (String, Int)) => (x._2, x._1))
        mapRdd
            .sortByKey(ascending = false)
            .map((x: (Int, String)) => (x._2, x._1))
            .collect().foreach(println(_: (String, Int)))
    }

    def sampleRdd(sparkContext: SparkContext): Unit = {
        val rdd: RDD[Int] = sparkContext.makeRDD(0 until 100)
        println(rdd.sample(false, 0.1).collect().mkString(", "))
        println(rdd.sample(false, 0.1).collect().mkString(", "))

        println(rdd.sample(false, 0.1, 100).collect().mkString(", "))
        println(rdd.sample(false, 0.1, 100).collect().mkString(", "))

        println(rdd.sample(true, 0.1, 200).collect().mkString(", "))
        println(rdd.sample(true, 0.1, 200).collect().mkString(", "))
    }

    def setOptRdd(sparkContext: SparkContext): Unit = {
        val rdd1: RDD[Int] = sparkContext.makeRDD(Seq(2, 3, 5))
        val rdd2: RDD[Int] = sparkContext.makeRDD(Seq(3, 5, 7))
        val unionRdd: RDD[Int] = rdd1.union(rdd2)
        val interRdd: RDD[Int] = rdd1.intersection(rdd2)
        val subRdd: RDD[Int] = rdd1.subtract(rdd2)
        val cartRdd: RDD[(Int, Int)] = rdd1.cartesian(rdd2)
        val zipRdd: RDD[(Int, Int)] = rdd1.zip(rdd2)
        val distinctRdd: RDD[Int] = unionRdd.distinct()

        println(s"unionRdd: ${unionRdd.collect().mkString(", ")}")
        println(s"interRdd: ${interRdd.collect().mkString(", ")}")
        println(s"subRdd: ${subRdd.collect().mkString(", ")}")
        println(s"cartRdd: ${cartRdd.collect().mkString(", ")}")
        println(s"zipRdd: ${zipRdd.collect().mkString(", ")}")
        println(s"distinctRdd: ${distinctRdd.collect().mkString(", ")}")
    }

    def cogroupRdd(sparkContext: SparkContext): Unit = {
        val rdd1: RDD[(String, Int)] = sparkContext.makeRDD(List(("leBron", 23), ("curry", 30), ("harden", 13)))
        val rdd2: RDD[(String, String)] = sparkContext.makeRDD(List(("durant", "Brooklyn"), ("leBron", "LosAngeles"), ("curry", "GoldenState")))

        // 将 key 相同的数据聚合到一个迭代器
        val cogroupRdd: RDD[(String, (Iterable[Int], Iterable[String]))] = rdd1.cogroup(rdd2)
        cogroupRdd.collect().foreach((item: (String, (Iterable[Int], Iterable[String]))) => {
            val value1: String = item._2._1.mkString(", ")
            val value2: String = item._2._2.mkString(", ")
            println(s"(${item._1}, (${value1}), (${value2}))")
        })
    }

    def joinRdd(sparkContext: SparkContext): Unit = {
        val rdd1: RDD[(String, Int)] = sparkContext.makeRDD(List(("leBron", 23), ("curry", 30), ("harden", 13)))
        val rdd2: RDD[(String, String)] = sparkContext.makeRDD(List(("durant", "Brooklyn"), ("leBron", "LosAngeles"), ("curry", "GoldenState")))

        // 将 key 相同的数据聚合到一个元组
        val joinRdd: RDD[(String, (Int, String))] = rdd1.join(rdd2)
        joinRdd.collect().foreach(println)
        val leftJoinRdd: RDD[(String, (Int, Option[String]))] = rdd1.leftOuterJoin(rdd2)
        leftJoinRdd.collect().foreach(println)
        val fullJoinRdd: RDD[(String, (Option[Int], Option[String]))] = rdd1.fullOuterJoin(rdd2)
        fullJoinRdd.collect().foreach(println)
    }
}
