package com.pain.core.spark.sql

import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession, TypedColumn}

object UdfFunctionApp {
    def main(args: Array[String]): Unit = {
        val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

        import spark.implicits._

        val upperFunc: UserDefinedFunction = spark.udf.register("upper", (text: String) => text.toUpperCase())
        val dataFrame: DataFrame = spark.read.json("io/json/student.json")
        dataFrame.createOrReplaceTempView("student")
        val studentDataFrame: DataFrame = spark.sql("select id, upper(name) as name, score from student")
        studentDataFrame.show()
        dataFrame.select($"name", upperFunc($"name").as("upperName")).show()
        dataFrame.select(expr("upper(name)")).show()
        spark.stop()
    }

    /**
     * 弱类型
     */
    def aggFuncUdfTest(spark: SparkSession): Unit = {
        spark.udf.register("avgFunc", AverageFunction)
        val dataFrame: DataFrame = spark.read.json("io/json/student.json")
        dataFrame.createOrReplaceTempView("student")
        val studentDataFrame: DataFrame = spark.sql("select avgFunc(score) from student")
        studentDataFrame.show()
    }

    /**
     * 强类型
     */
    def aggUdfTest(spark: SparkSession): Unit = {
        import spark.implicits._
        val dataSet: Dataset[Student] = spark.read.json("io/json/student.json").as[Student]
        val avgScore: TypedColumn[Student, Double] = AverageAgg.toColumn.name("avg_score")
        val scoreDataSet: Dataset[Double] = dataSet.select(avgScore)
        scoreDataSet.show()
    }
}

case class Student(id: Long, name: String, score: Long)
case class Average(var sum: Long, var count: Long)
