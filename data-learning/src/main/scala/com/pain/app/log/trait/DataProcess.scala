package com.pain.app.log.`trait`

import org.apache.spark.sql.SparkSession

trait DataProcess {
    def process(spark: SparkSession)
}
