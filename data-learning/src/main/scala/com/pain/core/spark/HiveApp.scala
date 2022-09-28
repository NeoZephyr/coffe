package com.pain.core.spark

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

import java.util.Properties

object HiveApp {

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().enableHiveSupport().getOrCreate()
    hiveDataSource(spark)
    spark.stop()
  }

  def hiveDataSource(spark: SparkSession): Unit = {
    import spark.implicits._

    val config: Config = ConfigFactory.load()
    val url: String = config.getString("db.default.url")
    val user: String = config.getString("db.default.user")
    val password: String = config.getString("db.default.password")
    val database: String = config.getString("db.default.database")
    val table: String = config.getString("db.default.table")

    val properties: Properties = new Properties()
    properties.put("user", user)
    properties.put("password", password)

    val dataFrame: DataFrame = spark.read.jdbc(url, s"$database.$table", properties)
    dataFrame.filter($"role_main" === "射手").select($"name", $"hp_max".as("hp"), $"role_main".as("role"))

    dataFrame.write.mode(SaveMode.Overwrite).saveAsTable("test_db.heros")
  }
}
