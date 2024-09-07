package com.pain.ink;

import com.pain.ink.bean.Access;
import com.pain.ink.bean.User;
import com.pain.ink.function.AccessParallelSource;
import com.pain.ink.function.AccessSource;
import com.pain.ink.function.UserSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.apache.flink.util.NumberSequenceIterator;

import java.util.Locale;

public class StreamApp {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // socketSource(env);
        // collectionSource(env);
        // kafkaSource(env);

        // accessSource(env);
        // accessParallelSource(env);
        userSource(env);
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

    public static void accessSource(StreamExecutionEnvironment env) {
        // parallelism 固定为 1
        DataStreamSource<Access> stream = env.addSource(new AccessSource());
        System.out.println("source parallelism: " + stream.getParallelism());

        // CPU 8 核，默认并行为 8
        stream.print().setParallelism(4);
    }

    public static void accessParallelSource(StreamExecutionEnvironment env) {
        DataStreamSource<Access> stream = env.addSource(new AccessParallelSource()).setParallelism(6);
        System.out.println("source parallelism: " + stream.getParallelism());

        // CPU 8 核，默认并行为 8
        stream.print();
    }

    public static void userSource(StreamExecutionEnvironment env) {
        DataStreamSource<User> stream = env.addSource(new UserSource()).setParallelism(5);
        System.out.println("source parallelism: " + stream.getParallelism());

        // CPU 8 核，默认并行为 8
        stream.print();
    }

    // nc -l -p 8020
    public static void count(StreamExecutionEnvironment env) {
        DataStreamSource<String> source = env.socketTextStream("localhost", 8020);
        source.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String value, Collector<String> out) throws Exception {
                String[] words = value.split(",");

                for (String word : words) {
                    out.collect(word.toLowerCase(Locale.ROOT).trim());
                }
            }
        }).filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String value) throws Exception {
                return StringUtils.isNotBlank(value);
            }
        }).map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String value) throws Exception {
                // Tuple2.of(value, 1);
                return new Tuple2<>(value, 1);
            }
        }).keyBy(new KeySelector<Tuple2<String, Integer>, String>() {
            @Override
            public String getKey(Tuple2<String, Integer> value) throws Exception {
                return value.f0;
            }
        })
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                // .window(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)))
                .sum(1).print();
    }
}
