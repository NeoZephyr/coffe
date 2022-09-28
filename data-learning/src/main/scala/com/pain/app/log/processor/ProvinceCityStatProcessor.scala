package com.pain.app.log.processor

import com.pain.app.log.`trait`.DataProcess
import com.pain.app.log.utils.{KuduUtils, SQLUtils, SchemaUtils, TableUtils}
import org.apache.spark.sql.{DataFrame, SparkSession}

object ProvinceCityStatProcessor extends DataProcess {
    override def process(spark: SparkSession): Unit = {
        val master = "cdh"
        val sourceTableName = TableUtils.getTableName("ods", spark)
        val sinkTableName = TableUtils.getTableName("province_city_stat", spark)
        val dataFrame: DataFrame = spark.read.format("org.apache.kudu.spark.kudu")
            .option("kudu.master", master)
            .option("kudu.table", sourceTableName)
            .load()

        dataFrame.createOrReplaceTempView("ods")
        val provinceCityDataFrame: DataFrame = spark.sql(SQLUtils.PROVINCE_CITY_SQL)

        KuduUtils.sink(provinceCityDataFrame, sinkTableName, master, SchemaUtils.ProvinceCitySchema, "provincename")
    }
}
