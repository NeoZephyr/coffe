package com.pain

import org.apache.flink.table.api.{EnvironmentSettings, TableEnvironment}

object TableApp {
    def main(args: Array[String]): Unit = {
        val settings = EnvironmentSettings
            .newInstance()
            .inBatchMode()
            .build()
        val tableEnv: TableEnvironment = TableEnvironment.create(settings)
    }
}
