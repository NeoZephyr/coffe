package com.pain.core.spark.sql

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions.{col, lit}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql._

import scala.collection.JavaConversions._

object DataFrameApp {

    def main(args: Array[String]): Unit = {
        val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

        import spark.implicits._

        // createFromSeq1(spark)
        // createFromSeq2(spark)
        // createFromCsv(spark)
        // createFromMysql(spark)
        // createFromRow(spark)

        // createFromClass(spark)

        // join(spark)

        // val people: DataFrame = spark.read.json("input/people.json")
        // show(people)
        // select(spark, people)
        // filter(spark, people)
        // view(spark, people)
//        people
//            .filter(people.col("age") > 25)
//            .withColumnRenamed("name", "nick")
//            .withColumn("province", col("city"))
//            .withColumn("country", lit("China"))
//            .show(false)
//
//
//        val peopleDs: Dataset[Person] = people.as[Person]
//        peopleDs.show()
        Seq(1, 100, 1000).toDS().show()

        spark.read
          .option("encoding", "UTF-8")
          .option("header", value = true)
          .option("multiLine", value = true)
          .csv("input/student.csv")
          .show()

        spark.stop()
    }

    case class Person(name: String, age: Long)

    def createFromSeq1(spark: SparkSession): Unit = {
        val seq: Seq[(String, Int)] = Seq(("Bob", 14), ("Alice", 18))
        val rdd: RDD[(String, Int)] = spark.sparkContext.parallelize(seq)
        val schema: StructType = StructType(StructField("name", StringType) ::
            StructField("age", IntegerType) :: Nil)
        val rowRDD: RDD[Row] = rdd.map(x => Row(x._1, x._2))
        val dataFrame: DataFrame = spark.createDataFrame(rowRDD, schema)
        dataFrame.show()
    }

    def createFromSeq2(spark: SparkSession): Unit = {
        import spark.implicits._

        val seq: Seq[(String, Int)] = Seq(("Bob", 14), ("Alice", 18))
        val rdd: RDD[(String, Int)] = spark.sparkContext.parallelize(seq)

        // seq.toDF("name", "age")
        val dataFrame: DataFrame = rdd.toDF("name", "age")
        dataFrame.show()
    }

    def createFromRow(spark: SparkSession): Unit = {
        val rows = 1.to(100).map(i => {
            val name = s"${System.currentTimeMillis()}-$i"
            val age = new Integer(i)
            RowFactory.create(name, age)
        })

        val structType = StructType(Seq(
            StructField("name", StringType),
            StructField("age", IntegerType)))

        val dataFrame = spark.createDataFrame(rows, structType)
        dataFrame.show()
    }

    def createFromClass(spark: SparkSession): Unit = {
        import spark.implicits._

        val seq: Seq[(String, Int)] = Seq(("Bob", 14), ("Alice", 18))
        val rdd: RDD[(String, Int)] = spark.sparkContext.parallelize(seq)
        val clazzRdd = rdd.map(x => {
            Student(x._1, x._2)
        })
        val dataFrame = clazzRdd.toDF("name", "age")
        val dataSet = clazzRdd.toDS()
        dataFrame.show()
        dataSet.show()
    }

    case class Student(name: String, age: Integer)

    def createFromCsv(spark: SparkSession): Unit = {
        val schema: StructType = StructType(StructField("name", StringType) ::
            StructField("age", IntegerType) :: Nil)
        val dataFrame: DataFrame = spark.read.format("csv")
            .option("header", value = true)
            .option("mode", "permissive")
            .schema(schema)
            .load("input/student.csv")
        dataFrame.show()
    }

    def createFromMysql(spark: SparkSession): Unit = {
        val driver = "com.mysql.jdbc.Driver"
        val url = "jdbc:mysql://localhost:3306/app?useSSL=false"
        val username = "root"
        val password = "123456"
        val sqlQuery = "(select * from user) as u"

        val dataFrame: DataFrame = spark.read.format("jdbc")
            .option("driver", driver)
            .option("url", url)
            .option("user", username)
            .option("password", password)
            .option("numPartitions", 20)
            .option("dbtable", sqlQuery)
            .load()
        dataFrame.show()
    }

    def show(dataFrame: DataFrame): Unit = {
        dataFrame.printSchema()
        dataFrame.show()
        dataFrame.show(3, 10, vertical = true)
        dataFrame.head(3).foreach(println)
        dataFrame.take(3).foreach(println)
    }

    def select(spark: SparkSession, dataFrame: DataFrame): Unit = {
        import spark.implicits._

        dataFrame.select("name").show()
        dataFrame.select($"name").show()
        dataFrame.select($"name", ($"age" + 10).as("+10")).show()
    }

    def filter(spark: SparkSession, dataFrame: DataFrame): Unit = {
        import spark.implicits._
        dataFrame.filter("age > 21").show()
        dataFrame.filter($"age" > 21).show()
        dataFrame.groupBy("age").count().show()
    }

    def view(spark: SparkSession, dataFrame: DataFrame): Unit = {
        dataFrame.createOrReplaceTempView("people")
        spark.sql("select * from people where age > 21").show()

        dataFrame.createOrReplaceGlobalTempView("people")
        spark.sql("select * from global_temp.people where age > 21").show()
    }

    def join(spark: SparkSession): Unit = {
        import spark.implicits._

        val seq1 = Seq(
            (1, "Mike", 28, "Male"),
            (2, "Lily", 30, "Female"),
            (3, "Raymond", 26, "Male"),
            (5, "Jack", 37, "Female")
        )
        val seq2 = Seq((1, 26000), (2, 30000), (4, 25000), (3, 20000))
        val employees: DataFrame = seq1.toDF("id", "name", "age", "gender")
        val salaries: DataFrame = seq2.toDF("id", "salary")

        val innerFrame: DataFrame = salaries.join(employees, salaries("id") === employees("id"), "inner")
        innerFrame.show()

        // left | leftouter |left_outer
        val leftFrame: DataFrame = salaries.join(employees, salaries("id") === employees("id"), "left")
        leftFrame.show()

        // full | outer | fullouter | full_outer
        val fullFrame: DataFrame = salaries.join(employees, salaries("id") === employees("id"), "full")
        fullFrame.show()

        // leftsemi | left_semi
        val semiFrame: DataFrame = salaries.join(employees, salaries("id") === employees("id"), "left_semi")
        semiFrame.show()

        // leftanti | left_anti
        val antiFrame: DataFrame = salaries.join(employees, salaries("id") === employees("id"), "left_anti")
        antiFrame.show()
    }
}
