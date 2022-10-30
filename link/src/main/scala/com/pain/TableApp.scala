package com.pain

object TableApp {
    def main(args: Array[String]): Unit = {
        val settings = EnvironmentSettings
            .newInstance()
            .inBatchMode()
            .build()
        val tableEnv: TableEnvironment = TableEnvironment.create(settings)
    }
}
