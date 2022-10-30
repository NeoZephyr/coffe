package com.pain

import com.pain.source.{FooNonParallelSourceFunction, FooParallelSourceFunction, FooRichParallelSourceFunction}
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time

object StreamingCountApp {
    def main(args: Array[String]): Unit = {
        val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
        // socket(env)

        // nonParallelFunction(env)
        // parallelFunction(env)
        // richParallelFunction(env)

        union(env)
    }

    def nonParallelFunction(env: StreamExecutionEnvironment): Unit = {
        // 并行度必须为 1
        val data: DataStream[Long] = env.addSource(new FooNonParallelSourceFunction).setParallelism(1)

        // CPU 8 核，默认并行为 8
        data.print().setParallelism(1)

        env.execute("nonParallel")
    }

    def parallelFunction(env: StreamExecutionEnvironment): Unit = {
        val data: DataStream[Long] = env.addSource(new FooParallelSourceFunction).setParallelism(3)
        data.print()

        env.execute("parallel")
    }

    def richParallelFunction(env: StreamExecutionEnvironment): Unit = {
        val data: DataStream[Long] = env.addSource(new FooRichParallelSourceFunction).setParallelism(3)
        data.print()

        env.execute("richParallel")
    }

    def union(env: StreamExecutionEnvironment): Unit = {
        val data1: DataStream[Long] = env.addSource(new FooNonParallelSourceFunction).setParallelism(1)
        val data2: DataStream[Long] = env.addSource(new FooParallelSourceFunction).setParallelism(3)

        data1.union(data2).print()

        env.execute("union")
    }

    def select(env: StreamExecutionEnvironment): Unit = {
        val data: DataStream[Long] = env.addSource(new FooNonParallelSourceFunction).setParallelism(1)

        data.union()
    }

    def socket(env: StreamExecutionEnvironment): Unit = {
        val stream: DataStream[String] = env.socketTextStream("localhost", 9999)

        import org.apache.flink.api.scala._

        stream.flatMap(_.split("\\s+"))
            .filter(_.nonEmpty)
            .map((_, 1))
            .keyBy(_._1)
            // .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
            .window(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)))
            .sum(1)
            .print()
            .setParallelism(1)

        env.execute("streaming")
    }
}
