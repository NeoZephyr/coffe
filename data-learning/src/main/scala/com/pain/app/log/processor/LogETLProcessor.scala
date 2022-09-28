package com.pain.app.log.processor

import com.pain.app.log.`trait`.DataProcess
import com.pain.app.log.utils.{IPUtils, KuduUtils, SQLUtils, SchemaUtils, TableUtils}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

object LogETLProcessor extends DataProcess {
    override def process(spark: SparkSession): Unit = {
        import spark.implicits._

        var rawPath: String = spark.sparkContext.getConf.get("spark.raw.path")
        var ipPath: String = spark.sparkContext.getConf.get("spark.ip.path")

        if (rawPath.isEmpty) {
            rawPath = "/Users/pain/Documents/bigdata/spark/spark-learning/input/log.json"
        }

        if (ipPath.isEmpty) {
            ipPath = "/Users/pain/Documents/bigdata/spark/spark-learning/input/ip.txt"
        }

        val rdd: RDD[String] = spark.sparkContext.textFile(ipPath)
        val ipDF: DataFrame = rdd.map(line => {
            val items: Array[String] = line.split("\\|")
            val startIp: Long = items(2).toLong
            val endIp: Long = items(3).toLong
            val province: String = items(6).toString
            val city: String = items(7).toString
            val isp: String = items(9).toString
            (startIp, endIp, province, city, isp)
        }).toDF("start_ip", "end_ip", "province", "city", "isp")

        var logDF: DataFrame = spark.read.json(rawPath)

        import org.apache.spark.sql.functions._

        def getLongIp() = udf((ip: String) => {
            IPUtils.ipToLong(ip)
        })

        logDF = logDF.withColumn("ip_long", getLongIp()($"ip"))

        // logDF.join(ipDF, logDF("ip_long").between(ipDF("start_ip"), ipDF("end_ip"))).show(false)

        logDF.createOrReplaceTempView("logs")
        ipDF.createOrReplaceTempView("ips")

        val result: DataFrame = spark.sql(SQLUtils.SQL)

        val tableName = TableUtils.getTableName("ods", spark)
        def master = "cdh"

        KuduUtils.sink(result, tableName, master, SchemaUtils.ODSSchema, "ip")
    }
}
