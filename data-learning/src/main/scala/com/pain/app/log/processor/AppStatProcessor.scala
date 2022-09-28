package com.pain.app.log.processor

import com.pain.app.log.`trait`.DataProcess
import com.pain.app.log.utils.{KuduUtils, SQLUtils, SchemaUtils, TableUtils}
import org.apache.spark.sql.{DataFrame, SparkSession}

object AppStatProcessor extends DataProcess{
    override def process(spark: SparkSession): Unit = {
        val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()
        val master = "cdh"
        val sourceTableName = TableUtils.getTableName("ods", spark)
        val sinkTableName = TableUtils.getTableName("app_stat", spark)
        val dataFrame: DataFrame = spark.read.format("org.apache.kudu.spark.kudu")
            .option("kudu.master", master)
            .option("kudu.table", sourceTableName)
            .load()

        dataFrame.createOrReplaceTempView("ods")

        val appTempDF: DataFrame = spark.sql(SQLUtils.APP_SQL_STEP1)
        appTempDF.createOrReplaceTempView("app_tmp")

        val appDF: DataFrame = spark.sql(SQLUtils.APP_SQL_STEP2)

        KuduUtils.sink(appDF, sinkTableName, master, SchemaUtils.APPSchema, "appid")
    }
}
