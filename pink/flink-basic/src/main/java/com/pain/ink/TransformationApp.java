package com.pain.ink;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class TransformationApp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // mapTransformation(env);
        flatMapTransformation(env);
        env.execute("stream");
    }

    public static void mapTransformation(StreamExecutionEnvironment env) {
        FileSource<String> source = FileSource.forRecordStreamFormat(
                new TextLineInputFormat("UTF-8"),
                new Path("input/access.txt")
        ).build();
        DataStreamSource<String> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");
        System.out.println("source parallelism: " + stream.getParallelism());
        stream.print();

        SingleOutputStreamOperator<Access> mapStream = stream.map(new MapFunction<String, Access>() {
            @Override
            public Access map(String s) throws Exception {
                String[] items = s.split(",");
                return new Access(items[0], items[1], Integer.parseInt(items[2]));
            }
        });
        mapStream.print();
    }

    public static void flatMapTransformation(StreamExecutionEnvironment env) {
        FileSource<String> source = FileSource.forRecordStreamFormat(
                new TextLineInputFormat("UTF-8"),
                new Path("input/words.txt")
        ).build();
        DataStreamSource<String> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");
        System.out.println("source parallelism: " + stream.getParallelism());

        SingleOutputStreamOperator<String> flatMapStream = stream.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                String[] items = s.split(",");

                for (String item : items) {
                    collector.collect(item);
                }
            }
        });

        flatMapStream.print();
    }

    static class Access {
        String time;
        String domain;
        int traffic;

        public Access(String time, String domain, int traffic) {
            this.time = time;
            this.domain = domain;
            this.traffic = traffic;
        }

        @Override
        public String toString() {
            return "Access{" +
                    "time='" + time + '\'' +
                    ", domain='" + domain + '\'' +
                    ", traffic='" + traffic + '\'' +
                    '}';
        }
    }
}
