package com.pain.app.etl

import org.apache.spark.sql.functions.{col, lit, sum}
import org.apache.spark.sql.{DataFrame, Row}

import scala.io.{BufferedSource, Source}

object ETLApp {

    val extractFields1: Seq[Row] => Seq[(String, Int)] = {
        (rows: Seq[Row]) => {
            var fields: Seq[(String, Int)] = Seq[(String, Int)]()
            rows.foreach((row: Row) => {
                fields = fields :+ (row.getString(0), row.getInt(1))
            })
            fields
        }
    }

    // 执行性能更高
    val extractFields2: Seq[Row] => Seq[(String, Int)] = {
        (rows: Seq[Row]) => {
            rows.map((row: Row) => (row.getString(0), row.getInt(1)))
        }
    }

    def createInstance(factDF: DataFrame, startDate: String, endData: String): DataFrame = {
        val instance: DataFrame = factDF
            .filter(col("eventDate") > lit(startDate) && col("eventDate") <= lit(endData))
            .groupBy("dim1", "dim2", "dim3", "eventDate")
            .agg(sum("value") as "sum_value")
        instance
    }

    val pairDF: DataFrame = null // small
    val factDF: DataFrame = null // big

    // createInstance 会被调用几百次 => 大数据 data frame 被反复扫描几百次
    pairDF.collect().foreach {
        case Row(startDate: String, endDate: String) =>
            val instance: DataFrame = createInstance(factDF, startDate, endDate)
            instance.write.parquet("")
        case _ =>
    }

    // 把时间区间罗列出来，转换为 broadcast hash join 会更加快
    // (startDate, endDate) = ("2021-01-01", "2021-01-31")
    // (startDate, endDate, eventDate) = ("2021-01-01", "2021-01-31"，"2021-01-01"), ... ("2021-01-01", "2021-01-31"，"2021-01-31")

    // 使用 Nested Loop Join
    // Nested Loop Join < (Merge Join, Hash Join, Broadcast Join)
    val instance: DataFrame = factDF
        .join(pairDF, factDF("eventDate") > pairDF("startDate") && factDF("eventDate") <= pairDF("endDate"))
        .groupBy("dim1", "dim2", "dim3", "eventDate", "startDate", "endDate")
        .agg(sum("value") as "sum_value")
    instance.write.partitionBy("endDate", "startDate").parquet("")

    // 建立字典的过程在每一个 Executor 中都需要执行
    def findIndex(path: String, hobby: String): Int = {
        val source: BufferedSource = Source.fromFile(path, "UTF-8")
        val lines: Array[String] = source.getLines().toArray
        source.close()
        val searchMap: Map[String, Int] = lines.zip(lines.indices).toMap
        searchMap.getOrElse(hobby, -1)
    }

    // 函数定义为高阶函数，形参是模板文件路径，返回结果是从用户兴趣到索引的函数
    // 建立字典的过程只在 Driver 端计算一次
    val findIndex: String => String => Int = {
        (path: String) =>
            val source: BufferedSource = Source.fromFile(path, "UTF-8")
            val lines: Array[String] = source.getLines().toArray
            source.close()
            val searchMap: Map[String, Int] = lines.zip(lines.indices).toMap
            (hobby: String) => searchMap.getOrElse(hobby, -1)
    }
}
