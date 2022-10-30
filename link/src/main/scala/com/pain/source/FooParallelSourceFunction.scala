package com.pain.source

import org.apache.flink.streaming.api.functions.source.{ParallelSourceFunction, SourceFunction}

class FooParallelSourceFunction extends ParallelSourceFunction[Long] {

    var count = 1L
    var isRunning = true

    override def run(sourceContext: SourceFunction.SourceContext[Long]): Unit = {
        while (isRunning) {
            sourceContext.collect(count)
            count += 1
            Thread.sleep(500)
        }
    }

    override def cancel(): Unit = {
        isRunning = false
    }
}
