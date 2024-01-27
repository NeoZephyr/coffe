package com.pain.core.hbase

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.filter.{FilterList, PrefixFilter}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{HFileOutputFormat2, LoadIncrementalHFiles, TableInputFormat, TableOutputFormat}
import org.apache.hadoop.hbase.protobuf.ProtobufUtil
import org.apache.hadoop.hbase.spark.HBaseContext
import org.apache.hadoop.hbase.util.{Base64, Bytes}
import org.apache.hadoop.hbase.{Cell, CellUtil, HColumnDescriptor, HTableDescriptor, KeyValue, TableName}
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.text.DecimalFormat
import java.util
import scala.collection.JavaConversions._
import scala.util.Random

object HBaseApp {

    /**
     * status
     * version
     *
     * list 'user.*'
     * put 'user', '1', 'info:job', 'thief'
     * put 'user', '1', 'info:name', 'jack'
     * put 'user', '1', 'info:name', 'jackson'
     *
     * get 'user', '1'
     * get 'user', '1', {COLUMN => 'info:name'}
     *
     * delete 'user', '1', 'info:name'
     *
     * truncate 'user'
     */
    def main(args: Array[String]): Unit = {
        val configuration = new Configuration()
        configuration.set("hbase.rootdir", "hdfs://cdp:8020/hbase")
        configuration.set("hbase.zookeeper.quorum", "cdp:2181")

        val connection: Connection = ConnectionFactory.createConnection(configuration)
        val admin: Admin = connection.getAdmin

        // createTable(admin, "user")
        // listTable(admin)
        // putData(connection, "user")
        // updateData(connection, "user")
        // getData(connection, "user")
        // scanData(connection, "user")
        // filterData(connection, "user")

        // writeDfV1(configuration, admin)
        // readDf(configuration)

        // writeByPut(connection)
        // writeByHadoopDataset(configuration)
        writeByHadoopFile(configuration, connection)
    }

    def createTable(admin: Admin, tableName: String): Unit = {
        val table: TableName = TableName.valueOf(tableName)

        if (admin.tableExists(table)) {
            println(s"table $tableName already exists")
            return
        }

        val descriptor = new HTableDescriptor(table)
        descriptor.addFamily(new HColumnDescriptor("info"))
        descriptor.addFamily(new HColumnDescriptor("address"))
        admin.createTable(descriptor)
        println(s"table $tableName create success")
    }

    def listTable(admin: Admin): Unit = {
        val descriptors: Array[HTableDescriptor] = admin.listTables()
        descriptors.foreach((descriptor: HTableDescriptor) => {
            println(descriptor.getNameAsString)
            val columnFamilies: Array[HColumnDescriptor] = descriptor.getColumnFamilies
            columnFamilies.foreach((cf: HColumnDescriptor) => {
                println("\t" + cf.getNameAsString)
            })
        })
    }

    def putData(connection: Connection, tableName: String): Unit = {
        val puts = new util.ArrayList[Put]()
        var put = new Put(Bytes.toBytes("meilb"))
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"), Bytes.toBytes("18"))
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("company"), Bytes.toBytes("apple"))
        put.addColumn(Bytes.toBytes("address"), Bytes.toBytes("country"), Bytes.toBytes("CN"))
        put.addColumn(Bytes.toBytes("address"), Bytes.toBytes("province"), Bytes.toBytes("HB"))
        puts.add(put)

        put = new Put(Bytes.toBytes("pei"))
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"), Bytes.toBytes("19"))
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("company"), Bytes.toBytes("ta"))
        put.addColumn(Bytes.toBytes("address"), Bytes.toBytes("country"), Bytes.toBytes("CN"))
        put.addColumn(Bytes.toBytes("address"), Bytes.toBytes("province"), Bytes.toBytes("SC"))
        puts.add(put)

        val table: Table = connection.getTable(TableName.valueOf(tableName))
        table.put(puts)
    }

    def updateData(connection: Connection, tableName: String): Unit = {
        val put = new Put(Bytes.toBytes("meilb"))
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"), Bytes.toBytes("30"))
        val table: Table = connection.getTable(TableName.valueOf(tableName))
        table.put(put)
    }

    def getData(connection: Connection, tableName: String): Unit = {
        val table: Table = connection.getTable(TableName.valueOf(tableName))
        val get = new Get(Bytes.toBytes("meilb"))
        get.addFamily(Bytes.toBytes("address"))
        // get.addColumn(Bytes.toBytes("address"), Bytes.toBytes("country"))
        get.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"))
        val result: Result = table.get(get)

        printResult(result)
    }

    def scanData(connection: Connection, tableName: String): Unit = {
        val table: Table = connection.getTable(TableName.valueOf(tableName))
        val scan = new Scan()
        scan.addColumn(Bytes.toBytes("info"), Bytes.toBytes("company"))
        val scanner: ResultScanner = table.getScanner(scan)
        val results: util.Iterator[Result] = scanner.iterator()

        while (results.hasNext) {
            printResult(results.next())
        }
    }

    def filterData(connection: Connection, tableName: String): Unit = {
        val table: Table = connection.getTable(TableName.valueOf(tableName))
        val scan = new Scan()

        val filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE)
        // val filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator("^p"))
        val filter = new PrefixFilter(Bytes.toBytes("m"))
        filterList.addFilter(new PrefixFilter(Bytes.toBytes("m")))
        filterList.addFilter(new PrefixFilter(Bytes.toBytes("p")))
        // scan.setFilter(filter)
        scan.setFilter(filterList)

        val scanner: ResultScanner = table.getScanner(scan)
        val results: util.Iterator[Result] = scanner.iterator()

        while (results.hasNext) {
            printResult(results.next())
        }
    }

    def printResult(result: Result): Unit = {
        result.rawCells().foreach((cell: Cell) => {
            println(
                s"${Bytes.toString(CellUtil.cloneRow(cell))}\t"
                    + s"${Bytes.toString(CellUtil.cloneFamily(cell))}\t"
                    + s"${Bytes.toString(CellUtil.cloneQualifier(cell))}\t"
                    + s"${Bytes.toString(CellUtil.cloneValue(cell))}\t"
                    + s"${cell.getTimestamp}")
        })
    }

    def readDf(configuration: Configuration): Unit = {
        val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

        configuration.set(TableInputFormat.INPUT_TABLE, "user")
        val scan = new Scan()
        scan.addFamily(Bytes.toBytes("info"))
        scan.addColumn(Bytes.toBytes("info"), Bytes.toBytes("name"))
        scan.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"))
        configuration.set(TableInputFormat.SCAN, Base64.encodeBytes(ProtobufUtil.toScan(scan).toByteArray))
        val hbaseRdd: RDD[(ImmutableBytesWritable, Result)] = spark.sparkContext.newAPIHadoopRDD(
            configuration,
            classOf[TableInputFormat],
            classOf[ImmutableBytesWritable],
            classOf[Result])
        val resultRdd: RDD[String] = hbaseRdd.map(x => {
            Bytes.toString(x._2.getValue(Bytes.toBytes("info"), Bytes.toBytes("name")))
        })
        resultRdd.collect().foreach(println)
    }

    // 1000 * 100             150295ms, 150s
    // 10000 * 10              98532ms, 98s
    // 100000                  92481ms, 92s

    // 1000000                857708ms,  857s
    // 100000 * 10            885158ms,  885s
    // 10000 * 100           1289826ms, 1289s

    // 10000000              8879259ms, 8879s
    def writeByPut(connection: Connection): Unit = {
        val random = new Random
        val df = new DecimalFormat("0000000000")
        val table: Table = connection.getTable(TableName.valueOf("test"))

        val start = System.currentTimeMillis()

        for (i <- 0 until 10000) {
            val puts = new util.ArrayList[Put]()

            for (_ <- 0 until 10) {
                val rk: String = df.format(random.nextInt(100000000))
                val code: String = df.format(random.nextInt(100000000))
                val put: Put = new Put(rk.getBytes)

                put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("code"), Bytes.toBytes(code))
                puts.add(put)
                table.put(puts)
            }

            println(s"round: $i")
        }
        val end = System.currentTimeMillis()

        println(s"cost: ${end - start}")
    }

    // 100 * 1000             22487ms,   22s
    // 10 * 10000              6000ms,    6s
    // 100000                  3478ms,    3s

    // 100 * 10000             30898ms,  30s
    // 10 * 100000             15007ms,  15s
    // 1000000                 16407ms,  16s

    // 100 * 100000           112741ms, 112s
    // 10 * 1000000           143796ms, 143s
    def writeByHadoopDataset(configuration: Configuration): Unit = {
        configuration.set(TableOutputFormat.OUTPUT_TABLE, "test")

        val spark: SparkSession = SparkSession.builder()
          .master("local")
          .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
          .getOrCreate()
        val job = Job.getInstance(configuration)
        job.setOutputKeyClass(classOf[ImmutableBytesWritable])
        job.setOutputValueClass(classOf[Put])
        job.setOutputFormatClass(classOf[TableOutputFormat[ImmutableBytesWritable]])

        val random = new Random
        val df = new DecimalFormat("0000000000")

        val start = System.currentTimeMillis()

        for (i <- 0 until 1) {
            val puts = new util.ArrayList[Put]()

            for (_ <- 0 until 10000000) {
                val rk: String = df.format(random.nextInt(100000000))
                val code: String = df.format(random.nextInt(100000000))
                val put: Put = new Put(rk.getBytes)

                put.addColumn(Bytes.toBytes("cf"), Bytes.toBytes("code"), Bytes.toBytes(code))
                puts.add(put)
            }

            val data: RDD[(ImmutableBytesWritable, Put)] = spark.sparkContext.makeRDD(puts).map(x => {
                (new ImmutableBytesWritable, x)
            })

            data.saveAsNewAPIHadoopDataset(job.getConfiguration)

            println(s"round: $i")
        }
        val end = System.currentTimeMillis()

        println(s"cost: ${end - start}")

        spark.sparkContext.stop()
    }

    // 100000                  4294ms,    4s
    // 1000000                10486ms,   10s
    def writeByHadoopFile(configuration: Configuration, connection: Connection): Unit = {
        val spark: SparkSession = SparkSession.builder()
          .master("local")
          .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
          .getOrCreate()
        val stagingPath = "hdfs://cdp:8020/test"
        val random = new Random
        val df = new DecimalFormat("0000000000")
        val start = System.currentTimeMillis()
        val sources = new util.ArrayList[(String, String)]()

        for (_ <- 0 until 1000) {
            val rk: String = df.format(random.nextInt(100000000))
            val code: String = df.format(random.nextInt(100000000))
            sources.add((rk, code))
        }

        val data: RDD[(ImmutableBytesWritable, KeyValue)] = spark.sparkContext.makeRDD(sources).mapPartitions(partition => {
            partition.map(x => {
                (
                  new ImmutableBytesWritable(Bytes.toBytes(x._1)),
                  new KeyValue(Bytes.toBytes(x._1), Bytes.toBytes("cf"), Bytes.toBytes("code"), Bytes.toBytes(x._2))
                )
            })
        }).sortByKey()

        import org.apache.hadoop.hbase.spark.HBaseRDDFunctions.GenericHBaseRDDFunctions

        // configuration.set(TableOutputFormat.OUTPUT_TABLE, "test")

        // data.hbaseBulkLoad(new HBaseContext(spark.sparkContext, configuration), TableName.valueOf("test"), null, stagingPath)

        data.saveAsNewAPIHadoopFile(
            stagingPath,
            classOf[ImmutableBytesWritable],
            classOf[KeyValue],
            classOf[HFileOutputFormat2],
            configuration
        )

        val table = connection.getTable(TableName.valueOf("test"))
        val job = Job.getInstance(configuration)
        job.setMapOutputKeyClass(classOf[ImmutableBytesWritable])
        job.setMapOutputValueClass(classOf[KeyValue])
        HFileOutputFormat2.configureIncrementalLoadMap(job, table)
        // HFileOutputFormat2.configureIncrementalLoad(job, table.getTableDescriptor, table.asInstanceOf[HTable].getRegionLocator)

        if (FileSystem.get(configuration).exists(new Path(stagingPath))) {
            val files = new LoadIncrementalHFiles(configuration)
            files.doBulkLoad(new Path(stagingPath), table.asInstanceOf[HTable])
            // files.doBulkLoad(new Path(stagingPath), connection.getAdmin, table, connection.getRegionLocator(TableName.valueOf("text")))
            FileSystem.get(configuration).delete(new Path(stagingPath), true)
        }

        val end = System.currentTimeMillis()

        println(s"cost: ${end - start}")

        spark.stop()
    }

    def writeByHadoopFile(configuration: Configuration, admin: Admin): Unit = {
        val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()
        val people: DataFrame = spark.read.json("input/people.json")

        val putRDD: RDD[(ImmutableBytesWritable, Put)] = people.rdd.map(x => {
            val id: String = x.getAs[String]("id")
            val name: String = x.getAs[String]("name")
            val age: Long = x.getAs[Long]("age")

            val put = new Put(Bytes.toBytes(id))
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("name"), Bytes.toBytes(name))
            put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"), Bytes.toBytes(age))

            // 不写 WAL，而是手工刷新 MemStore
            put.setDurability(Durability.SKIP_WAL)
            (new ImmutableBytesWritable(Bytes.toBytes(id)), put)
        })

        configuration.set(TableOutputFormat.OUTPUT_TABLE, "people")

        putRDD.saveAsNewAPIHadoopFile(
            "hdfs://cdp:8020/hbase",
            classOf[ImmutableBytesWritable],
            classOf[Put],
            classOf[TableOutputFormat[ImmutableBytesWritable]],
            configuration)

        // MemStore => StoreFile
        admin.flush(TableName.valueOf("people"))

        spark.stop()
    }

}
