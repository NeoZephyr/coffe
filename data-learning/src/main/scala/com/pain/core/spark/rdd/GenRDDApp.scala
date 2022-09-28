package com.pain.core.spark.rdd

import org.apache.spark.rdd.{JdbcRDD, RDD}
import org.apache.spark.{SparkConf, SparkContext}

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

object GenRDDApp {
    def main(args: Array[String]): Unit = {
        val sparkConf = new SparkConf().setAppName("create rdd").setMaster("local[*]")
        val sparkContext = new SparkContext(sparkConf)

        // genFromArray(sparkContext)
        // genFromFile(sparkContext)
        // genFromMysql(sparkContext)
        writeToMysql(sparkContext)
    }

    def genFromArray(sparkContext: SparkContext): Unit = {
        val array: Array[Int] = Array(1, 2, 3, 4, 5)
        // sparkContext.makeRDD(array, 5)
        val rdd: RDD[Int] = sparkContext.parallelize(array, 5)
        println(s"partitionNumber: ${rdd.partitions.length}")
        println(rdd.collect().mkString(", "))
    }

    def genFromFile(sparkContext: SparkContext): Unit = {
        val rdd: RDD[String] = sparkContext.textFile("input/words.txt")
        println(s"partitionNumber: ${rdd.partitions.length}")
        println(rdd.collect().mkString(", "))
    }


    def textTest(sparkContext: SparkContext): Unit = {
        val rdd: RDD[String] = sparkContext.textFile("io/text/words.txt")
        rdd.collect().foreach(println)
        val wordCountRdd: RDD[(String, Int)] = rdd.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)
        wordCountRdd.saveAsTextFile("io/text/output")
    }

    def genFromMysql(sparkContext: SparkContext): Unit = {
        val driver = "com.mysql.jdbc.Driver"
        val url = "jdbc:mysql://cdh:3306/app?useSSL=false"
        val username = "root"
        val password = "123456"

        val rdd = new JdbcRDD(sparkContext, () => {
            Class.forName(driver)
            DriverManager.getConnection(url, username, password)
        },
            "select * from `customer` where `id` >= ? and `id` <= ?;",
            1,
            2,
            1,
            (result: ResultSet) => (result.getString(3), result.getString(4))
        )

        rdd.collect().foreach(println)
    }

    def writeToMysql(sparkContext: SparkContext): Unit = {
        val rdd: RDD[(String, Int)] = sparkContext.makeRDD(List(("taylor", 28), ("pain", 31)))

        rdd.foreachPartition((it: Iterator[(String, Int)]) => {
            val driver = "com.mysql.jdbc.Driver"
            val url = "jdbc:mysql://cdh:3306/app"
            val username = "root"
            val password = "123456"
            Class.forName(driver)
            val connection: Connection = DriverManager.getConnection(url, username, password)

            it.foreach((stu: (String, Int)) => {
                val statement: PreparedStatement = connection.prepareStatement("insert into customer(name, age) values (?, ?)")
                statement.setString(1, stu._1)
                statement.setInt(2, stu._2)
                statement.execute()
            })
        })
    }
}
