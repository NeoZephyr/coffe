package com.pain.ink;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.NumberSequenceIterator;

public class StreamApp {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // socketSource(env);
        // collectionSource(env);
        kafkaSource(env);
        env.execute("stream");
    }

    public static void socketSource(StreamExecutionEnvironment env) {
        env.setParallelism(3);

        // 并行度永远为 1
        DataStreamSource<String> source = env.socketTextStream("localhost", 9999);
        System.out.println("source parallelism: " + source.getParallelism());

        SingleOutputStreamOperator<String> filterStream = source.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String value) throws Exception {
                return !"spark".equals(value);
            }
        }).setParallelism(5);
        System.out.println("filter stream parallelism: " + filterStream.getParallelism());
    }

    public static void collectionSource(StreamExecutionEnvironment env) {
        DataStreamSource<Long> source = env.fromParallelCollection(new NumberSequenceIterator(1, 10), Long.class);
        System.out.println("source parallelism: " + source.getParallelism());

        SingleOutputStreamOperator<Long> filterStream = source.filter(new FilterFunction<Long>() {
            @Override
            public boolean filter(Long value) throws Exception {
                return value % 2 == 0;
            }
        });
        System.out.println("filter stream parallelism: " + filterStream.getParallelism());

        filterStream.print();
    }

    public static void kafkaSource(StreamExecutionEnvironment env) {
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("lab:9092")
                .setTopics("test")
                .setGroupId("testGroup")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStreamSource<String> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

        System.out.println("source parallelism: " + stream.getParallelism());
        stream.print("task").setParallelism(2);
    }
}
