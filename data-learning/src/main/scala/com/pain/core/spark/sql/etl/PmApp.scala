package com.pain.core.spark.sql.etl

import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.functions._

object PmApp {

    /**
     * http://stateair.net/web/post/1/1.html
     */
    def main(args: Array[String]): Unit = {
        val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

        def getGrade(value: Int): String = {
            if (value <= 50 && value >= 0) {
                "健康"
            } else if (value <= 100) {
                "中等"
            } else if (value <= 150) {
                "对敏感人群不健康"
            } else if (value <= 200) {
                "不健康"
            } else if (value <= 300) {
                "非常不健康"
            } else if (value <= 500) {
                "危险"
            } else if (value > 500) {
                "爆表"
            } else {
                "未知"
            }
        }

        val gradeUdf: UserDefinedFunction = udf((value: Int) => getGrade(value))

        process(spark, gradeUdf, 2008, "pm/Beijing_2008_HourlyPM2.5_created20140325.csv")
        process(spark, gradeUdf, 2009, "pm/Beijing_2009_HourlyPM25_created20140709.csv")
        process(spark, gradeUdf, 2010, "pm/Beijing_2010_HourlyPM25_created20140709.csv")
        process(spark, gradeUdf, 2011, "pm/Beijing_2011_HourlyPM25_created20140709.csv")
        process(spark, gradeUdf, 2012, "pm/Beijing_2012_HourlyPM2.5_created20140325.csv")
        process(spark, gradeUdf, 2013, "pm/Beijing_2013_HourlyPM2.5_created20140325.csv")
        process(spark, gradeUdf, 2014, "pm/Beijing_2014_HourlyPM25_created20150203.csv")
        process(spark, gradeUdf, 2015, "pm/Beijing_2015_HourlyPM25_created20160201.csv")
        process(spark, gradeUdf, 2016, "pm/Beijing_2016_HourlyPM25_created20170201.csv")
        process(spark, gradeUdf, 2017, "pm/Beijing_2017_HourlyPM25_created20170803.csv")

        spark.stop()
    }

    def process(spark: SparkSession, gradeUdf: UserDefinedFunction, year: Int, path: String): Unit = {
        var df: DataFrame = spark.read.format("csv").option("header", "true").option("inferSchema", "true").load(path)
        df = df.withColumn("grade", gradeUdf(df("value")))
        df.show()
        val gradeDf: DataFrame = df.groupBy("grade").count()
        gradeDf.show()

        val resDf: DataFrame = gradeDf.select("grade", "count").withColumn("percent", gradeDf("count") / df.count() * 100)
        resDf.show()

        resDf.write.format("org.elasticsearch.spark.sql")
            .option("es.nodes", "cdh").option("es.nodes.discovery", "false")
            .option("es.port", "9200").option("es.nodes.wan.only", "true")
            .mode(SaveMode.Overwrite)
            .save(s"weather${year}")
    }
}
