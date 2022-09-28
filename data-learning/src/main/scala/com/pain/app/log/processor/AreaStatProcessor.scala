package com.pain.app.log.processor

import com.pain.app.log.`trait`.DataProcess
import com.pain.app.log.utils.{KuduUtils, SQLUtils, SchemaUtils, TableUtils}
import org.apache.spark.sql.{DataFrame, SparkSession}

object AreaStatProcessor extends DataProcess {
    override def process(spark: SparkSession): Unit = {
        val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()
        val master = "cdh"
        val sourceTableName = TableUtils.getTableName("ods", spark)
        val sinkTableName = TableUtils.getTableName("area_stat", spark)
        val dataFrame: DataFrame = spark.read.format("org.apache.kudu.spark.kudu")
            .option("kudu.master", master)
            .option("kudu.table", sourceTableName)
            .load()

        dataFrame.createOrReplaceTempView("ods")

        val areaTempDF: DataFrame = spark.sql(SQLUtils.AREA_SQL_STEP1)
        areaTempDF.createOrReplaceTempView("area_tmp")

        val areaDF: DataFrame = spark.sql(SQLUtils.AREA_SQL_STEP2)

        KuduUtils.sink(areaDF, sinkTableName, master, SchemaUtils.AREASchema, "provincename")
    }
}