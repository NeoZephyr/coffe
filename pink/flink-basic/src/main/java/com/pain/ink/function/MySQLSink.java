package com.pain.ink.function;

import com.pain.ink.utils.DbUtils;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class MySQLSink extends RichSinkFunction<Tuple2<String, Integer>> {
    Connection connection;
    PreparedStatement insert;
    PreparedStatement update;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        connection = DbUtils.getConnection();
        insert = connection.prepareStatement("insert into traffic(domain, traffic) values(?, ?)");
        update = connection.prepareStatement("update traffic set traffic = ? where domain = ?");
    }

    @Override
    public void close() throws Exception {
        super.close();

        if (insert != null) {
            insert.close();
        }

        if (update != null) {
            update.close();
        }

        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public void invoke(Tuple2<String, Integer> value, Context context) throws Exception {
        update.setInt(1, value.f1);
        update.setString(2, value.f0);
        update.execute();

        if (update.getUpdateCount() == 0) {
            insert.setString(1, value.f0);
            insert.setInt(2, value.f1);
            insert.execute();
        }
    }
}