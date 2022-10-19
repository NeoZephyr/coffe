package com.pain.core.spark.sql.etl

import com.alibaba.excel.EasyExcelFactory
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.event.AnalysisEventListener
import com.pain.bean.{Customer, Insurer, User}
import com.pain.support.JsonUtil
import org.apache.commons.lang3.StringUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions.{col, lit, to_date}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, RowFactory, SaveMode, SparkSession}

import java.io.File
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util
import scala.collection.JavaConversions._


object WeiboApp {

    def main(args: Array[String]): Unit = {
        val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

        val dir = new File("input/location")
        val files = dir.listFiles()
        val insurers = new util.ArrayList[Insurer]()

        for (file <- files) {
            println(s"=== ${file.getName}")
            readExcel(file, insurers)
        }

//        var dataFrame = spark.read.parquet("input/cache")
//
//        println(dataFrame.count())
//        dataFrame.printSchema()
//        dataFrame.show()
//
//        dataFrame.repartition(1).write.mode(SaveMode.Overwrite).parquet("input/user/stage")
        // etlPolicePublish(spark)
    }

    def etlWeibo(spark: SparkSession): Unit = {
        val dataFrame = spark.read.text("input/weibo/*.txt")
        val rows: RDD[Row] = dataFrame.rdd.flatMap(row => {
            val words = row.getString(0).split("\\s+")

            if (words.length == 2) {
                Some(Row(words(0), words(1)))
            } else {
                None
            }
        })
        val structType = StructType(Seq(
            StructField("mobile", StringType),
            StructField("weibo_id", StringType)))
        val ds = spark.createDataFrame(rows, structType).dropDuplicates("mobile")

        ds.show(10)
        ds.repartition(1).write.mode(SaveMode.Overwrite).parquet("input/user")
    }

    def etlJD(spark: SparkSession): Unit = {
        val dataFrame = spark.read.text("input/location/*.txt")
        val rows: RDD[Row] = dataFrame.rdd.flatMap(row => {
            val words = row.getString(0).split("\\s+")

            if (words.length == 2) {
                Some(Row(words(0), words(1)))
            } else {
                None
            }
        })
        val structType = StructType(Seq(
            StructField("mobile", StringType),
            StructField("weibo_id", StringType)))
        val ds = spark.createDataFrame(rows, structType).dropDuplicates("mobile")

        ds.show(10)
        ds.repartition(1).write.mode(SaveMode.Overwrite).parquet("input/user")
    }

    def etlCensus(spark: SparkSession): Unit = {
        val dir = new File("input/location")
        val files = dir.listFiles()
        val users = new util.ArrayList[User]()

        for (file <- files) {
            println(s"=== ${file.getName}")

            readCensusExcel(file, users)
        }

        println(s"=== total count: ${users.size()}, ${JsonUtil.objToStr(users.get(0))}")

        val rdd: RDD[User] = spark.sparkContext.makeRDD(users, 4)
        val rowRdd: RDD[Row] = rdd.map(u => {
            // RowFactory.create(u.name, u.mobile, u.idCard, u.gender, u.address, u.birthday, u.statDate)
            RowFactory.create(u.name, u.mobile, u.idCard, u.gender, u.address, u.birthday, u.statDate)
        })
        val structType = StructType(Seq(
            StructField("name", StringType),
            StructField("mobile", StringType),
            StructField("id_card", StringType),
            StructField("gender", StringType),
            StructField("address", StringType),
            StructField("birthday", TimestampType),
            StructField("statDate", TimestampType)))

        var dataFrame = spark.createDataFrame(rowRdd, structType)
        // dataFrame = dataFrame.withColumn("birthday", lit(null).cast(TimestampType))
        // dataFrame = dataFrame.withColumn("gender", lit(null).cast(StringType))
        dataFrame = dataFrame.withColumn("birthday", to_date(col("birthday")))
        dataFrame.show()
        dataFrame.printSchema()

        dataFrame.repartition(1).write.mode(SaveMode.Overwrite).parquet("input/user/stage")
    }

    def readCensusExcel(file: File, users: util.ArrayList[User]): Unit = {
        // TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        val datetimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
        var count = 0
        var ignoreCount = 0

        EasyExcelFactory.read(file, new AnalysisEventListener[java.util.Map[Int, String]] {

            override def invoke(data: util.Map[Int, String], analysisContext: AnalysisContext): Unit = {
                count = count + 1
                val user = new User
                val statDate = data.get(1)
                val birthday = data.get(4)

                if (!StringUtils.isBlank(statDate)) {
                    try {
                        user.statDate = new Timestamp(datetimeFormat.parse(statDate).getTime)
                    } catch {
                        case _: Exception =>
                    }
                }

                if (!StringUtils.isBlank(birthday)) {
                    try {
                        user.birthday = new Timestamp(dateFormat.parse(birthday).getTime)
                    } catch {
                        case _: Exception =>
                    }
                }

                if (StringUtils.equals("男", data.get(5))
                    || StringUtils.equals("女", data.get(5))) {
                    user.gender = data.get(5)
                }

                if (StringUtils.isNotBlank(data.get(6)) && data.get(6).matches("[0-9a-zA-Z-]{18}")) {
                    user.idCard = data.get(6)
                }

                if (StringUtils.isNotBlank(data.get(7)) && !data.get(7).matches("\\d+") && data.get(7).length <= 8) {
                    user.name = data.get(7)
                }

                if (StringUtils.isNotBlank(data.get(8)) && data.get(8).matches("[\\d+]{11}")) {
                    user.mobile = data.get(8)
                }

                if (StringUtils.isNotBlank(data.get(9)) && !data.get(9).matches("\\d+")) {
                    user.address = data.get(9)
                }

                if (!StringUtils.isAllBlank(user.name, user.mobile)) {
                    users.add(user)
                } else {
                    ignoreCount = ignoreCount + 1
                    println(s"=== count: $count, ignoreCount: $ignoreCount, data: ${JsonUtil.objToStr(data)}")
                }
            }

            override def doAfterAllAnalysed(analysisContext: AnalysisContext): Unit = {
            }
        }).sheet().doRead()
    }

    def readExcel(file: File, insurers: util.ArrayList[Insurer]): Unit = {
        // TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
        var count = 0
        var ignoreCount = 0

        EasyExcelFactory.read(file, new AnalysisEventListener[java.util.Map[Int, String]] {

            override def invoke(data: util.Map[Int, String], analysisContext: AnalysisContext): Unit = {
                val insurer = new Insurer
                count = count + 1

                if (StringUtils.isNotBlank(data.get(1)) && !data.get(1).matches("\\d+") && data.get(1).length <= 8) {
                    insurer.name = data.get(1)
                }

                if (StringUtils.isNotBlank(data.get(2)) && data.get(2).matches("[0-9a-zA-Z-]{18}")) {
                    insurer.idCard = data.get(2)
                }

                if (StringUtils.equals("男", data.get(3))
                    || StringUtils.equals("女", data.get(3))) {
                    insurer.gender = data.get(3)
                }

                if (StringUtils.isNotBlank(data.get(4)) && data.get(4).matches("[\\d+]{11}")) {
                    insurer.mobile = data.get(4)
                }

                if (StringUtils.isNotBlank(data.get(5)) && !data.get(5).matches("\\d+")) {
                    insurer.email = data.get(5)
                }

                if (StringUtils.isNotBlank(data.get(6)) && !data.get(6).matches("\\d+")) {
                    insurer.province = data.get(6)
                }

                if (StringUtils.isNotBlank(data.get(7)) && !data.get(7).matches("\\d+")) {
                    insurer.city = data.get(7)
                }

                if (StringUtils.isNotBlank(data.get(8)) && !data.get(8).matches("\\d+")) {
                    insurer.address = data.get(8)
                }

                if (StringUtils.isNotBlank(data.get(11)) && !data.get(11).matches("\\d+")) {
                    insurer.industry = data.get(11)
                }

                if (StringUtils.isNotBlank(data.get(12)) && !data.get(12).matches("\\d+")) {
                    insurer.salary = data.get(12)
                }

                val birthday = data.get(10)

                if (!StringUtils.isBlank(birthday)) {
                    try {
                        insurer.birthday = new Timestamp(dateFormat.parse(birthday).getTime)
                    } catch {
                        case _: Exception =>
                    }
                }

                if (StringUtils.equals("未婚", data.get(13))
                    || StringUtils.equals("已婚", data.get(13))) {
                    insurer.marriage = data.get(13)
                }

                if (StringUtils.isNotBlank(data.get(14)) && !data.get(14).matches("\\d+")) {
                    insurer.education = data.get(14)
                }

                if (StringUtils.isNotBlank(data.get(15)) && !data.get(15).matches("\\d+")) {
                    insurer.carBrand = data.get(15)
                }

                if (!StringUtils.isAllBlank(insurer.name, insurer.mobile)) {
                    insurers.add(insurer)
                } else {
                    ignoreCount = ignoreCount + 1
                    println(s"=== count: $count, ignoreCount: $ignoreCount, data: ${JsonUtil.objToStr(data)}")
                }
            }

            override def doAfterAllAnalysed(analysisContext: AnalysisContext): Unit = {
            }
        }).sheet().doRead()
    }
}
