package com.pain.core.spark.sql

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

import java.util.Properties

object DataSourceApp {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

    // textDataSource(spark)
    // jsonDataSource(spark)
    // commonDataSource(spark)
    // parquetDataSource(spark)
    // jdbcDataSource(spark)
    // configurableJdbcDataSource(spark)

    // convert(spark)
  }

  def textDataSource(spark: SparkSession): Unit = {
    import spark.implicits._

    val dataFrame: DataFrame = spark.read.text("/Users/pain/Documents/bigdata/spark/spark-learning/input/people.txt")
    val dataset: Dataset[(String, String)] = dataFrame.map(row => {
      val words: Array[String] = row.getString(0).split(",")
      (words(0), words(1).trim)
    })

    dataset.write.mode(SaveMode.Overwrite).text("output/text")
  }

  def jsonDataSource(spark: SparkSession): Unit = {
    import spark.implicits._

    val dataFrame: DataFrame = spark.read.json("/Users/pain/Documents/bigdata/spark/spark-learning/input/student.json")
    dataFrame.filter($"age" > 20).select($"name", $"age", $"score.math".as("math"))
      .write.mode(SaveMode.Overwrite).json("output/json")
  }

  def commonDataSource(spark: SparkSession): Unit = {
    import spark.implicits._

    val jsonFrame: DataFrame = spark.read.format("json").load("/Users/pain/Documents/bigdata/spark/spark-learning/input/student.json")
    jsonFrame.filter($"age" < 20).select($"name", $"age", $"score.math".as("math"))
      .write.format("json").mode(SaveMode.Overwrite).save("output/json")
  }

  def parquetDataSource(spark: SparkSession): Unit = {

    val dataFrame: DataFrame = spark.read.parquet("/Users/pain/Documents/bigdata/spark/spark-learning/input/users.parquet")
    dataFrame.printSchema()
    dataFrame.show()

    dataFrame.select("name", "favorite_color").write.option("compression", "none").mode(SaveMode.Overwrite).parquet("output/parquet")

    spark.read.parquet("output/parquet").show()
  }

  def jdbcDataSource(spark: SparkSession): Unit = {
    import spark.implicits._

    val url = "jdbc:mysql://cdh:3306?useUnicode=yes&characterEncoding=UTF-8"
    val properties: Properties = new Properties()
    properties.put("user", "root")
    properties.put("password", "123456")
    properties.put("sessionInitStatement", "SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;")

    val dataFrame: DataFrame = spark.read.jdbc(url, "nba.heros", properties)

    dataFrame.filter($"role_main" === "战士").select($"name", $"hp_max".as("hp"), $"role_main".as("role"))
      .write.mode(SaveMode.Overwrite).jdbc(url, "nba.heros2", properties)
  }

  def configurableJdbcDataSource(spark: SparkSession): Unit = {
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
    dataFrame.filter($"role_main" === "坦克").select($"name", $"hp_max".as("hp"), $"role_main".as("role")).show()
  }

  def convert(spark: SparkSession): Unit = {
    import spark.implicits._

    val dataFrame: DataFrame = spark.read.json("/Users/pain/Documents/bigdata/spark/spark-learning/input/student.json")
    dataFrame.filter($"age" >= 18).write.format("parquet").mode(SaveMode.Overwrite).save("output/parquet")
    spark.read.parquet("output/parquet").show()
  }
}
