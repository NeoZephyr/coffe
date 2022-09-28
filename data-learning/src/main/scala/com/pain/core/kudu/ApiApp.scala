package com.pain.core.kudu

import java.util

import org.apache.kudu.{ColumnSchema, Schema, Type}
import org.apache.kudu.client.{AlterTableOptions, CreateTableOptions, Insert, KuduClient, KuduScanner, KuduSession, KuduTable, PartialRow, RowResult, RowResultIterator, Update}

object ApiApp {
    def main(args: Array[String]): Unit = {
        val kudu_master = "cdh"
        val tableName = "word_count"
        val newTableName = "word_count_test"
        val kuduClient: KuduClient = new KuduClient.KuduClientBuilder(kudu_master).build()

        // createTable(kuduClient, "hero")
        // deleteTable(kuduClient, "province_city_stat")

        // insertTable(kuduClient, tableName)
        // queryTable(kuduClient, "ods")

        // updateTable(kuduClient, tableName)

        // renameTable(kuduClient, tableName, newTableName)
    }

    def createTable(client: KuduClient, tableName: String): Unit = {
        import scala.collection.JavaConverters._

        val columns = List(
            new ColumnSchema.ColumnSchemaBuilder("name", Type.STRING).key(true).build(),
            new ColumnSchema.ColumnSchemaBuilder("hp", Type.DOUBLE).build(),
            new ColumnSchema.ColumnSchemaBuilder("role", Type.STRING).build()
        ).asJava

        val schema = new Schema(columns)
        val options = new CreateTableOptions
        options.setNumReplicas(1)

        val partitionColumns: util.List[String] = List("name").asJava
        options.addHashPartitions(partitionColumns, 3)

        client.createTable(tableName, schema, options)
    }

    def deleteTable(client: KuduClient, tableName: String): Unit = {
        client.deleteTable(tableName)
    }

    def insertTable(client: KuduClient, tableName: String): Unit = {
        val table: KuduTable = client.openTable(tableName)
        val session: KuduSession = client.newSession()

        for (i <- 1 to 10) {
            val insert: Insert = table.newInsert()
            val row: PartialRow = insert.getRow
            row.addString("word", s"pain-$i")
            row.addInt("count", 100 + i)

            session.apply(insert)
        }
    }

    def queryTable(client: KuduClient, tableName: String): Unit = {
        val table: KuduTable = client.openTable(tableName)
        val scanner: KuduScanner = client.newScannerBuilder(table).build()

        while (scanner.hasMoreRows) {
            val iterator: RowResultIterator = scanner.nextRows()

            println("begin")

            while (iterator.hasNext) {
                val result: RowResult = iterator.next()
                println(result.getString("word") + " => " + result.getInt("count"))
            }

            println("end")
        }
    }

    def updateTable(client: KuduClient, tableName: String): Unit = {
        val table: KuduTable = client.openTable(tableName)
        val session: KuduSession = client.newSession()

        val update: Update = table.newUpdate()
        val row: PartialRow = update.getRow
        row.addString("word", "pain-2")
        row.addInt("count", 200)
        session.apply(update)
        session.close()
    }

    def renameTable(client: KuduClient, tableName: String, newTableName: String): Unit = {
        val options = new AlterTableOptions
        options.renameTable(newTableName)
        client.alterTable(tableName, options)
    }
}
