package com.pain.core.hbase

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{Cell, CellUtil, HColumnDescriptor, HTableDescriptor, TableName}
import org.apache.hadoop.hbase.client.{Admin, Connection, ConnectionFactory, Consistency, Get, Put, Result}
import org.apache.hadoop.hbase.util.Bytes

import java.util
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{CountDownLatch, Executors}
import scala.collection.JavaConversions._

object HBaseTest {

  def main(args: Array[String]): Unit = {
    val configuration = new Configuration()
    configuration.set("hbase.rootdir", "hdfs://cdp:8020/hbase")
    configuration.set("hbase.zookeeper.quorum", "cdp:2181")

    val connection: Connection = ConnectionFactory.createConnection(configuration)
    val admin: Admin = connection.getAdmin

    // testConsistency(connection)

    def table = connection.getTable(TableName.valueOf("u2i"))

    val get = new Get(Bytes.toBytes("150-2"))
    get.setConsistency(Consistency.TIMELINE)
    val result = table.get(get)

    println(s"isStale: ${result.isStale}")
  }

  def testConsistency(connection: Connection): Unit = {
    val service = Executors.newFixedThreadPool(5)

    for (j <- 1 to 2000) {
      val counter = new AtomicInteger(0)

      for (r <- 1 to 20) {
        val values = new util.ArrayList[String]()
        for (i <- 1 to 250) {
          values.add(s"m-$r-$i")
        }

        writeData(connection, "u2i", s"160-$j", values)

        counter.getAndAdd(250)
        val latch = new CountDownLatch(1)
        var res = true

        service.execute(new Runnable {
          override def run(): Unit = {
            res = readData(connection, "u2i", s"160-$j", counter.get())
            latch.countDown()
          }
        })

        latch.await()

        if (!res) {
          throw new RuntimeException("fuck=====")
        }
      }

      println(s"===== round $j complete")
    }

    service.shutdown()
  }

  def createTable(admin: Admin, tableName: String): Unit = {
    val table: TableName = TableName.valueOf(tableName)

    if (admin.tableExists(table)) {
      println(s"table $tableName already exists")
      return
    }

    val descriptor = new HTableDescriptor(table)
    descriptor.addFamily(new HColumnDescriptor("p"))
    descriptor.addFamily(new HColumnDescriptor("np"))
    admin.createTable(descriptor)
    println(s"table $tableName create success")
  }

  def writeData(connection: Connection, tableName: String, rowKey: String, values: java.util.List[String]): Unit = {
    val put = new Put(Bytes.toBytes(rowKey))

    values.foreach(v => {
      val qualifier = Bytes.toBytes(s"mobile\0$v")
      put.addColumn(Bytes.toBytes("p"), qualifier, Bytes.toBytes("1"))
    })

    def table = connection.getTable(TableName.valueOf(tableName))

    table.put(put)
    table.close()
  }

  def readData(connection: Connection, tableName: String, rowKey: String, expectCount: Int): Boolean = {
    def table = connection.getTable(TableName.valueOf(tableName))

    val get = new Get(Bytes.toBytes(rowKey))
    get.setConsistency(Consistency.TIMELINE)
    val result = table.get(get)

    if (result.isEmpty) {
      println(s"======= $rowKey is empty")
    } else {
      //      val tuples = result.rawCells().map(c => {
      //        (Bytes.toString(CellUtil.cloneQualifier(c)), Bytes.toString(CellUtil.cloneValue(c)))
      //      })

      val length = result.rawCells().length

      if (result.rawCells().length != expectCount) {
        println(s"======= $rowKey error, actual count: ${length}, expect count: ${expectCount}")
        printResult(result)
        return false
      }
    }

    true
  }

  def printResult(result: Result): Unit = {
    result.rawCells().foreach((cell: Cell) => {
      println(
        s"${Bytes.toString(CellUtil.cloneRow(cell))}\t"
          + s"${Bytes.toString(CellUtil.cloneQualifier(cell))}\t"
          + s"${cell.getTimestamp}")
    })
  }
}
