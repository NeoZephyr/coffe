package com.pain.ink;

import com.pain.ink.bean.Access;
import com.pain.ink.function.AccessRichMap;
import com.pain.ink.function.MySQLSink;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class SinkApp {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        sinkMysql(env);
        env.execute("sink");
    }

    public static void sinkMysql(StreamExecutionEnvironment env) {
        FileSource<String> source = FileSource.forRecordStreamFormat(
                new TextLineInputFormat("UTF-8"),
                new Path("input/access.txt")
        ).build();
        DataStreamSource<String> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "File Source");
        System.out.println("source parallelism: " + stream.getParallelism());
        stream.print();

        SingleOutputStreamOperator<Access> mapStream = stream.map(new AccessRichMap());

        stream.map(new AccessRichMap()).keyBy(new KeySelector<Access, String>() {
            @Override
            public String getKey(Access access) throws Exception {
                return access.getDomain();
            }
        }).sum("traffic").map(new MapFunction<Access, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(Access access) throws Exception {
                return Tuple2.of(access.getDomain(), access.getTraffic());
            }
        }).addSink(new MySQLSink());
    }
}
