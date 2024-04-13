package com.pain

import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.{SimpleStringEncoder, SimpleStringSchema}
import org.apache.flink.configuration.MemorySize
import org.apache.flink.connector.base.DeliveryGuarantee
import org.apache.flink.connector.file.sink.FileSink
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

import java.time.Duration

object ConnectorApp {

    def main(args: Array[String]): Unit = {
        val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

        fileConnector(env)
    }

    def fileConnector(env: StreamExecutionEnvironment): Unit = {
        val data: DataStream[String] = env.socketTextStream("localhost", 9999)
        data.print().setParallelism(1)

        val sink: FileSink[String] = FileSink
            .forRowFormat(new Path("output/file"), new SimpleStringEncoder[String]("UTF-8"))
            .withRollingPolicy(
                DefaultRollingPolicy.builder()
                    .withRolloverInterval(Duration.ofSeconds(10))
                    .withInactivityInterval(Duration.ofSeconds(10))
                    .withMaxPartSize(new MemorySize(100))
                    .build())
            .build()

        data.sinkTo(sink)
        env.execute("file")
    }

    def kafkaSource(env: StreamExecutionEnvironment): Unit = {
        env.enableCheckpointing(4000)
        env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
        env.getCheckpointConfig.setCheckpointTimeout(10000)
        env.getCheckpointConfig.setMaxConcurrentCheckpoints(1)

        import org.apache.flink.api.scala._


        val source: KafkaSource[String] = KafkaSource.builder[String]
            .setBootstrapServers("")
            .setTopics("input-topic")
            .setGroupId("my-group")
            .setStartingOffsets(OffsetsInitializer.earliest)
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .build

        val data: DataStream[String] = env.fromSource(source, WatermarkStrategy.noWatermarks[String], "Kafka Source")
        data.print().setParallelism(1)
    }

    def kafkaSink(env: StreamExecutionEnvironment): Unit = {
        val data: DataStream[String] = env.socketTextStream("localhost", 9999)

        val sink: KafkaSink[String] = KafkaSink.builder()
            .setBootstrapServers("")
            .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                .setTopic("topic-name")
                .setValueSerializationSchema(new SimpleStringSchema())
                .build()
            )
            .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
            .build()

        data.sinkTo(sink)
    }
}
