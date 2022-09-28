package com.pain.core.stream

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

object KafkaStreamApp {
  def main(args: Array[String]): Unit = {
  }

  def processStream(): Unit = {
    val sparkConf = new SparkConf().setMaster("local[2]")
      .setAppName("KafkaStreamApp")
      .set("spark.streaming.kafka.maxRatePerPartition", "100")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .registerKryoClasses(Array(classOf[String]))
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "brokers",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val stream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream(
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](List(""), kafkaParams))

    stream.foreachRDD(rdd => {
      val data = rdd.map(x => x.value())
      data.cache()

      data.unpersist(true)
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
