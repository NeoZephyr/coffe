package com.pain

import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time

object StreamingCountApp {
    def main(args: Array[String]): Unit = {
        val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
        StreamExecutionEnvironment.createLocalEnvironment()

        val stream: DataStream[String] = env.socketTextStream("localhost", 9999)

        import org.apache.flink.api.scala._

        stream.flatMap(_.split(" "))
            .filter(_.nonEmpty)
            .map((_, 1))
            .keyBy(_._1)
            .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
            .sum(1)
            .print()
            .setParallelism(1)

        env.execute("streaming")
    }
}
