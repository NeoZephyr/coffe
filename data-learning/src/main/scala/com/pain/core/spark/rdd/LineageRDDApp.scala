package com.pain.core.spark.rdd

import org.apache.spark.rdd.RDD
import org.apache.spark.{Dependency, SparkConf, SparkContext}

object LineageRDDApp {
    def main(args: Array[String]): Unit = {
        val sparkConf: SparkConf = new SparkConf().setAppName("lineage").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)
        val rdd: RDD[String] = sparkContext.textFile("input/words.txt")
        val wordsRdd: RDD[String] = rdd.flatMap((_: String).split(" "))
        val wordToCountRdd: RDD[(String, Int)] = wordsRdd.map(((_: String), 1))
        val wordToSumRdd: RDD[(String, Int)] = wordToCountRdd.reduceByKey((_: Int) + (_: Int))

        println("wordToSumRdd debugString")
        println(wordToSumRdd.toDebugString)
        printDependencies(wordToSumRdd)

        wordToSumRdd.collect().foreach(println)
    }

    def printDependencies(rdd: RDD[_]): Unit = {
        println(rdd)

        rdd.dependencies.foreach((item: Dependency[_]) => {
            printDependencies(item.rdd)
        })
    }
}
