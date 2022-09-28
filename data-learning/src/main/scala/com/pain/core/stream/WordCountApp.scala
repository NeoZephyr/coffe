package com.pain.core.stream

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext, Time}

import java.sql.{Connection, DriverManager}

object WordCountApp {

  def main(args: Array[String]): Unit = {
    // processSocket()
    // processStatefulSocket()
    // processFile()

    // processTransformSocket()
    processSqlSocket()
  }

  // nc -l -p 9999
  def processSocket(): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("SocketStream")
    val ssc = new StreamingContext(conf, Seconds(1))
    val lines = ssc.socketTextStream("localhost", 9999)
    val words = lines.flatMap(_.split(" "))
    val pairs = words.map(word => (word, 1))
    val wordCounts = pairs.reduceByKey(_ + _)
    wordCounts.print()
    wordCounts.foreachRDD(rdd => {
      rdd.foreachPartition(partition => {
        val connection: Connection = createConnection()
        partition.foreach(record => {
          val sql = s"insert into wc(word, count) values ('${record._1}', ${record._2}) on duplicate key update count = count + ${record._2}"
          connection.createStatement().execute(sql)
        })
        connection.close()
      })
    })

    ssc.start()
    ssc.awaitTermination()
  }

  def processStatefulSocket(): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("SocketStream")
    val ssc = new StreamingContext(conf, Seconds(1))
    ssc.checkpoint("output/stream")
    val lines = ssc.socketTextStream("localhost", 9999)
    val words = lines.flatMap(_.split(" "))
    val pairs = words.map(word => (word, 1))
    val state: DStream[(String, Int)] = pairs.updateStateByKey[Int](updateFunction _)
    state.print()

    ssc.start()
    ssc.awaitTermination()
  }

  def processTransformSocket(): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("SocketStream")
    val ssc = new StreamingContext(conf, Seconds(1))
    val blacks = List("a", "e")
    val blacksRDD = ssc.sparkContext.parallelize(blacks).map(x => (x, true))
    val lines = ssc.socketTextStream("localhost", 9999)
    val validLogs = lines.map(x => (x.split(",")(1), x)).transform(rdd => {
      rdd.leftOuterJoin(blacksRDD).filter(x => !x._2._2.getOrElse(false)).map(x => x._2._1)
    })

    validLogs.print()
    ssc.start()
    ssc.awaitTermination()
  }

  def processFile(): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("FileStream")
    val ssc = new StreamingContext(conf, Seconds(2))
    val lines = ssc.textFileStream("input/stream")
    val words = lines.flatMap(_.split(" "))
    val pairs = words.map(word => (word, 1))
    val wordCounts = pairs.reduceByKey(_ + _)
    wordCounts.print()

    ssc.start()
    ssc.awaitTermination()
  }

  def processSqlSocket(): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("SocketStream")
    val ssc = new StreamingContext(conf, Seconds(1))
    val lines = ssc.socketTextStream("localhost", 9999)
    val words = lines.flatMap(_.split(" "))
    words.foreachRDD { (rdd: RDD[String], time: Time) =>
      val spark = SparkSessionSingleton.getInstance(rdd.sparkContext.getConf)
      import spark.implicits._

      val wordsDf = rdd.map(w => Record(w)).toDF()
      wordsDf.createOrReplaceTempView("words")
      val wcDf: DataFrame = spark.sql("select word, count(*) as total from words group by word")
      wcDf.show()
    }

    ssc.start()
    ssc.awaitTermination()
  }

  case class Record(word: String)

  object SparkSessionSingleton {
    @transient private var instance: SparkSession = _

    def getInstance(sparkConf: SparkConf): SparkSession = {
      if (instance == null) {
        instance = SparkSession
          .builder
          .config(sparkConf)
          .getOrCreate()
      }
      instance
    }
  }

  private def updateFunction(currentValues: Seq[Int], preValues: Option[Int]): Option[Int] = {
    val current = currentValues.sum
    val pre = preValues.getOrElse(0)

    Some(current + pre)
  }

  private def createConnection() = {
    Class.forName("com.mysql.jdbc.Driver")
    DriverManager.getConnection("jdbc:mysql://cdh:3306/spark", "root", "123456")
  }
}
