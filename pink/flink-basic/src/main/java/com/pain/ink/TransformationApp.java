package com.pain.ink;

import com.pain.ink.bean.Access;
import com.pain.ink.function.AccessPartitioner;
import com.pain.ink.function.AccessRichMap;
import com.pain.ink.function.AccessSource;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;
import org.apache.flink.util.Collector;

public class TransformationApp {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // mapTransformation(env);
        // flatMapTransformation(env);
        // richMapTransformation(env);
        // unionTransformation(env);
        // connectTransformation(env);
        // coFlagMapTransformation(env);
        partition(env);
        env.execute("stream");
    }

    public static void mapTransformation(StreamExecutionEnvironment env) {
        FileSource<String> source = FileSource.forRecordStreamFormat(
                new TextLineInputFormat("UTF-8"),
                new Path("input/access.txt")
        ).build();
        DataStreamSource<String> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "File Source");
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
        DataStreamSource<String> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "File Source");
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

    public static void richMapTransformation(StreamExecutionEnvironment env) {
        FileSource<String> source = FileSource.forRecordStreamFormat(
                new TextLineInputFormat("UTF-8"),
                new Path("input/access.txt")
        ).build();
        DataStreamSource<String> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "File Source");
        System.out.println("source parallelism: " + stream.getParallelism());
        stream.print();

        SingleOutputStreamOperator<Access> mapStream = stream.map(new AccessRichMap());
        mapStream.print();
    }

    public static void unionTransformation(StreamExecutionEnvironment env) {
        // nc -l -p 9999
        DataStreamSource<String> stream1 = env.socketTextStream("localhost", 9999);
        DataStreamSource<String> stream2 = env.socketTextStream("localhost", 9998);
        stream1.union(stream2).print();
    }

    /**
     * union 多流合并，数据结构必须相同
     * connect 双流合并，数据结构可以不同
     */
    public static void connectTransformation(StreamExecutionEnvironment env) {
        // nc -l -p 9999
        DataStreamSource<String> stream1 = env.socketTextStream("localhost", 9999);
        DataStreamSource<String> stream2 = env.socketTextStream("localhost", 9998);

        // coMap 每个流单独处理
        SingleOutputStreamOperator<String> stream = stream1.connect(stream2).map(new CoMapFunction<String, String, String>() {
            @Override
            public String map1(String s) throws Exception {
                return "stream1 -> " + s;
            }

            @Override
            public String map2(String s) throws Exception {
                return "stream2 -> " + s;
            }
        });
        stream.print();
    }

    public static void coFlagMapTransformation(StreamExecutionEnvironment env) {
        DataStreamSource<String> stream1 = env.fromElements("a b c", "d e f");
        DataStreamSource<String> stream2 = env.fromElements("1,2,3", "4,5,6");

        stream1.connect(stream2).flatMap(new CoFlatMapFunction<String, String, String>() {
            @Override
            public void flatMap1(String s, Collector<String> collector) throws Exception {
                String[] splits = s.split(" ");

                for (String split : splits) {
                    collector.collect(split);
                }
            }

            @Override
            public void flatMap2(String s, Collector<String> collector) throws Exception {
                String[] splits = s.split(",");
                for (String split : splits) {
                    collector.collect(split);
                }
            }
        }).print();
    }

    public static void partition(StreamExecutionEnvironment env) {
        // parallelism 固定为 1
        DataStreamSource<Access> stream = env.addSource(new AccessSource());
        System.out.println("source parallelism: " + stream.getParallelism());

        // CPU 8 核，默认并行为 8
        stream.map(new MapFunction<Access, Tuple2<String, Access>>() {
            @Override
            public Tuple2<String, Access> map(Access access) throws Exception {
                return Tuple2.of(access.getDomain(), access);
            }
        })
                .partitionCustom(new AccessPartitioner(), 0)
                .map(new MapFunction<Tuple2<String, Access>, Access>() {
                    @Override
                    public Access map(Tuple2<String, Access> tuple) throws Exception {
                        return tuple.f1;
                    }
                }).print();
    }
}
