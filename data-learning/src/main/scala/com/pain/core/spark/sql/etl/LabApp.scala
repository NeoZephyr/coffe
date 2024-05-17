package com.pain.core.spark.sql.etl

import com.pain.support.JsonUtil
import org.apache.spark.SparkConf
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{IntegerType, StringType}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

import java.util.UUID
import scala.beans.BeanProperty
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._

object LabApp {

  def main(args: Array[String]): Unit = {
    // split()
    // split0()

    // val c: Map[String, Any] = Map("a" -> 10, "b" -> "xxxx")
    // println(JsonUtil.objToStr(c))
//    val map = Notifications.createRefreshEvent(10, ImpalaRefreshDto("campaign_event_facts", Seq(PartitionColumn("tenant_id", "10", "Long")).asJava)).asJava
//    println(map.get("publisher"))
//
//    kafkaSink()

    // sort()

    // collect()

    // hive(1)

    // recursive()

    // event()

    write()
  }

  // spark.hadoop.mapreduce.fileoutputcommitter.algorithm.version

  def event(): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()
    spark.sql("set spark.sql.session.timeZone = UTC")
    var df: DataFrame = spark.read.format("csv").option("header", "true").option("inferSchema", "true").load("input/d.csv")
    df.createOrReplaceTempView("x")

    spark.sql("select uid, to_timestamp(time / 1000) from x").show(false)


    // var sql = s"select identity_value as uid from customer_event where tenant_id = $maTenantId and customer_id < 0 and identity_type = '$identityType' and identity_value != '' group by identity_value"
    var sql = "select uid from x where cast(cid as long) < 0 and uid != '' and date > to_timestamp(111111) group by uid"
    spark.sql(sql).show()
    df.printSchema()
    df.show()
  }

  def recursive(): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()
    var df: DataFrame = spark.read.format("csv").option("header", "true").option("inferSchema", "true").load("input/c.csv")
    df.createOrReplaceTempView("x")

    import spark.implicits._

    val buffer = new ListBuffer[(String)]
    buffer.append(("10"))
    buffer.append(("100"))
    buffer.append(("99"))
    buffer.append(("1000"))

    val frame = buffer.toDF("cid")
    frame.createOrReplaceTempView("a")

    var dff = spark.sql("select from, to from a join x on a.cid = x.from")
    dff.createOrReplaceTempView("b")
    var count = dff.count()

    while (count > 0) {
      dff = spark.sql("select b.from, " +
        "case when x.to is null then b.to else x.to end as to, " +
        "case when x.to is null then 0 else 1 end as flag " +
        "from b left join x on b.to = x.from")
      dff.createOrReplaceTempView("b")
      dff.show()
      count = spark.sql("select 1 from b where b.flag = 1").count()
    }

    println("complete")
  }

  def hive(i: Int): Unit = {
    System.setProperty("HADOOP_USER_NAME", "ubuntu")
    val spark: SparkSession = SparkSession.builder()
      .master("local")
      .config("hive.metastore.uris", "thrift://cdp:9083")
      .config("spark.hadoop.dfs.replication", 1)
      .enableHiveSupport()
      .getOrCreate()

    spark.catalog.setCurrentDatabase("cdp")

    import spark.implicits._

    val buffer = new ListBuffer[(String, Int, Long)]
    val listId = 1000 * i
    val tenantId = i

    for (j <- 1.to(100)) {
      val customerId = s"${i * j}"
      buffer.append((customerId, tenantId, listId))
    }

    val frame = buffer.toDF("customer_id", "tenant_id", "list_id")
    val view = s"x_${i}"
    frame.createOrReplaceTempView(view)

    val sql =
      s"""
        INSERT OVERWRITE TABLE cdp.dynamic_list_member
        PARTITION(tenant_id = $tenantId, list_id = $listId)
        select customer_id from $view
      """

    spark.sql(sql)

    // spark.read.table("cdp.dynamic_list_member").show()
    // spark.sql("select * from cdp.dynamic_list_member").show()
  }

  def collect(): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()
    var df: DataFrame = spark.read.format("csv").option("header", "true").option("inferSchema", "true").load("input/a.csv")
    var dff: DataFrame = spark.read.format("csv").option("header", "true").option("inferSchema", "true").load("input/b.csv")
    df.createOrReplaceTempView("x")
    dff.createOrReplaceTempView("y")

    // union -> drop duplicate
    def c = spark.sql("select customer_id from x union all select customer_id from y").count()
    println(c)

    df = spark.read.format("csv").option("header", "true").option("inferSchema", "true").load("input/identity.csv")
    df.createOrReplaceTempView("x")
    df.show()

    // s"select * from(select count(1),collect_set(_uid) ids,_type,_value from ($odsIdentitySql) t group by _type,_value having count(1)>1) t1 where size(ids)>1"
    spark.sql("select _uid, count(1) as c, collect_set(_value) from x where _type = 'mobile' group by _uid having count(1) > 1").show()
  }

  // coalesce 问题
  // push down the coalesce operation
  // avoid push down by exec cache and count operation before coalesce operation
  //
  // 1. exec task(map, filter) -> merge partition
  // 2. merge partition -> exec task(map, filter)

  // CoalescedRDD
  // DefaultPartitionCoalescer

  // spark2.4 vs spark3.2        后者 sort 倾向于在同一个分区
  // sort 之后 join         sort 之后的顺序无效
  def sort(): Unit = {
    val conf = new SparkConf()
    conf.set("spark.sql.autoBroadcastJoinThreshold", "-1")
    conf.set("spark.sql.shuffle.partitions", "4")
    val spark: SparkSession = SparkSession.builder().config(conf).master("local").getOrCreate()
    var df: DataFrame = spark.read.format("csv").option("header", "true").option("inferSchema", "true").load("input/a.csv")
    var dff: DataFrame = spark.read.format("csv").option("header", "true").option("inferSchema", "true").load("input/b.csv")
    // df = df.repartition(200)
    // df = df.repartition(2)
    dff = dff.repartition(3)
    println(s"partition count: ${df.rdd.partitions.length}")
    df.rdd.mapPartitionsWithIndex((idx, iter) => {
      if (iter.isEmpty) {
        println("idx: %s is empty", idx)
      } else {
        printf("idx: %s, data: %s\n", idx, iter.next().mkString("---"))
      }
//      iter.foreach(r => {
//        printf("idx: %s, data: %s\n", idx, r.mkString("---"))
//      })
      iter
    }).count()
    df = df.withColumn("_id", (rand() * 1e6).cast(IntegerType)).sort("_id").coalesce(9)
    println("==========")
    println(s"partition count: ${df.rdd.partitions.length}")

    df.rdd.mapPartitionsWithIndex((idx, iter) => {
      iter.foreach(r => {
        printf("idx: %s, data: %s\n", idx, r.mkString("---"))
      })
      iter
    }).count()
    df.createOrReplaceTempView("x")
    dff.createOrReplaceTempView("y")
    // val ds = spark.sql("select * from x join y on x.customer_id = y.customer_id order by _id")
    val ds = spark.sql("select * from x join y on x.customer_id = y.customer_id")

    // val ds = spark.sql("select * from x")

    println(s"================================================================== ${ds.rdd.partitions.length}")

    ds.rdd.mapPartitionsWithIndex((idx, iter) => {
      iter.foreach(r => {
        printf("idx: %s, data: %s\n", idx, r.mkString("---"))
      })
      iter
    }).count()

    ds.createOrReplaceTempView("xx")
    println("===========")

    // def sample(withReplacement: Boolean, fraction: Double): Dataset[T] = {
    // ds.sample(withReplacement = false, 0.2).show()
    // spark.sql("select * from xx limit 5").show()
    // df.show()
  }

  def kafkaSink(): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()
    var df: DataFrame = spark.read.format("csv").option("header", "true").option("inferSchema", "true").load("input/identity.csv")

    val sinkFrame = df.select(coalesce(col("_type"), col("_value")).as("key"), to_json(struct(df("*"))).cast(StringType).as("value"))
    sinkFrame.show(false)
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

  def write(): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()
    var df: DataFrame = spark.read.format("csv").option("header", "true").option("inferSchema", "true").load("input/a.csv")
    df = df.repartition(5)
    df.write.csv("input/checkpoint/c/d/e")
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