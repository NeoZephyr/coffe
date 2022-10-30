package com.pain.source

import org.apache.flink.streaming.api.functions.source.{RichParallelSourceFunction, SourceFunction}

class FooRichParallelSourceFunction extends RichParallelSourceFunction[Long] {

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
