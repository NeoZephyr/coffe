package com.pain.core.spark.sql.etl

import com.pain.support.JsonUtil
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

import java.util.UUID
import scala.beans.BeanProperty

object LabApp {

  def main(args: Array[String]): Unit = {
    // split()
    // split0()
    import scala.collection.JavaConverters._

    val map = Notifications.createRefreshEvent(10, ImpalaRefreshDto("campaign_event_facts", Seq(PartitionColumn("tenant_id", "10", "Long")).asJava)).asJava
    println(map.get("publisher"))
    println(JsonUtil.objToStr(map))
  }

  def split0(): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()
    var df: DataFrame = spark.read.format("csv").option("header", "true").option("inferSchema", "true").load("input/a.csv")
    df = df.repartition(20)
    println(s"partitions: ${df.rdd.partitions.length}")
    df.createOrReplaceTempView("c")
    var cv = "c"

    for (i <- 0 until 20) {
      val view = s"split_${i + 1}"
      val count = 20 - i
      var sql = s"select customer_id from $cv limit ${count}"
      cv = s"c_${i + 1}"
      val ds = spark.sql(sql)
      ds.createOrReplaceTempView(cv)
      println(s"count: ${ds.count()}")
    }
  }

  def split(): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()
    spark.sparkContext.setCheckpointDir("input/checkpoint")
    var df: DataFrame = spark.read.format("csv").option("header", "true").option("inferSchema", "true").load("input/a.csv")
    df = df.repartition(20)
    df = df.checkpoint()
    createView(df, "c", true)
    println(s"partitions: ${df.rdd.partitions.length}, count: ${df.count()}")
    var cv = "c"

    for (i <- 0 until 20) {
      val view = s"split_${i + 1}"
      var sql = s"select customer_id from $cv limit 1"

      val ds = spark.sql(sql)
      createView(ds, view)
      println(s"=== $view")
      // ds.show()

      val leftSql = s"SELECT tl.customer_id FROM $cv tl LEFT " +
        s"JOIN $view tp ON tl.customer_id = tp.customer_id WHERE tp.customer_id IS NULL"

      var leftDs = spark.sql(leftSql)
      leftDs.explain()

      if (i % 5 == 0) {
        leftDs = leftDs.checkpoint()
      }
      cv = s"c${i + 1}"
      createView(leftDs, cv)
      val leftCount = leftDs.count()

      println(s"=== left count: $leftCount")
      // leftDs.show()
    }

//    spark.sparkContext.getCheckpointDir.foreach(dir => {
//      FileSystem.get(spark.sparkContext.hadoopConfiguration).delete(new Path(dir), true)
//    })
  }

  def createView(dataset: Dataset[Row], view: String, cache: Boolean = true): Unit = {
    dataset.createOrReplaceTempView(view)

    if (cache) {
      // dataset.persist(StorageLevel.MEMORY_AND_DISK)
    }
  }

}

object Notifications {

  def createRefreshEvent(tenantId: Long, dto: ImpalaRefreshDto): Map[String, Any] = {
    val event = Map[String, Any](
      "tenantId" -> tenantId,
      "dto" -> dto,
      "classType" -> "com.convertlab.foundation.library.business.eventbroker.ImpalaRefreshEvent",
      "publisher" -> "SPARKJOB",
      "subscriber" -> "ANY",
      "sourceGroupId" -> "SPARKJOB",
      "type" -> "DEFAULT_EVENT_TYPE",
      "subType" -> "DEFAULT_EVENT_SUBTYPE",
      "messageId" -> UUID.randomUUID().toString
    )
    event
  }
}

case class ImpalaRefreshDto(@BeanProperty tableName: String, @BeanProperty partitionColumns: java.util.List[PartitionColumn]) extends Serializable

case class PartitionColumn(@BeanProperty column: String, @BeanProperty value: String, @BeanProperty `type`: String) extends Serializable