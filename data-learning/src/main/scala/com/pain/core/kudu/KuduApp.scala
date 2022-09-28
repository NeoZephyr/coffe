package com.pain.core.kudu

import java.util.Properties

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.json.JSONObject

object KuduApp {
    def main(args: Array[String]): Unit = {
        val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

        read(spark)
        spark.stop()
    }

    def read(spark: SparkSession): Unit = {
        val config: Config = ConfigFactory.load()
        val kuduMaster: String = config.getString("kudu.master")

        spark.read.format("org.apache.kudu.spark.kudu")
            .option("kudu.master", kuduMaster)
            .option("kudu.table", "app_stat")
            .load().show()
    }

    def write(spark: SparkSession): Unit = {
        import spark.implicits._

        val config: Config = ConfigFactory.load()
        val url: String = config.getString("db.default.url")
        val user: String = config.getString("db.default.user")
        val password: String = config.getString("db.default.password")
        val database: String = config.getString("db.default.database")
        val table: String = config.getString("db.default.table")
        val kuduMaster: String = config.getString("kudu.master")

        val properties: Properties = new Properties()
        properties.put("user", user)
        properties.put("password", password)

        val dataFrame: DataFrame = spark.read.jdbc(url, s"$database.$table", properties)
        dataFrame
            .filter($"role_main" === "坦克")
            .select($"name", $"hp_max".as("hp"), $"role_main".as("role"))
            .write.mode(SaveMode.Append).format("org.apache.kudu.spark.kudu")
            .option("kudu.master", kuduMaster)
            .option("kudu.table", "hero").save()
    }
}
