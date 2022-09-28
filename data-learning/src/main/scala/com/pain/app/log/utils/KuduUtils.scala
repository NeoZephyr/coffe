package com.pain.app.log.utils

import java.util

import org.apache.kudu.Schema
import org.apache.kudu.client.{CreateTableOptions, KuduClient}
import org.apache.spark.sql.{DataFrame, SaveMode}

object KuduUtils {
    def sink(data: DataFrame,
             tableName: String,
             master: String,
             schema: Schema,
             partitionId: String): Unit = {
        val kuduClient: KuduClient = new KuduClient.KuduClientBuilder(master).build()
        val options = new CreateTableOptions
        options.setNumReplicas(1)

        val partitionColumns = new util.LinkedList[String]()
        partitionColumns.add(partitionId)
        options.addHashPartitions(partitionColumns, 3)

        if (kuduClient.tableExists(tableName)) {
            kuduClient.deleteTable(tableName)
        }

        kuduClient.createTable(tableName, schema, options)

        data.write.mode(SaveMode.Append)
            .format("org.apache.kudu.spark.kudu")
            .option("kudu.master", master)
            .option("kudu.table", tableName).save()
    }
}
