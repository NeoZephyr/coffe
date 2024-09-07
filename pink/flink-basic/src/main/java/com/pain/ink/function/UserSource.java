package com.pain.ink.function;

import com.pain.ink.bean.User;
import com.pain.ink.utils.DbUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserSource extends RichParallelSourceFunction<User> {

    Connection connection;
    PreparedStatement stmt;

    @Override
    public void open(Configuration parameters) throws Exception {
        connection = DbUtils.getConnection();
        stmt = connection.prepareStatement("select * from user");
    }

    @Override
    public void run(SourceContext<User> sourceContext) throws Exception {
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String name = rs.getString("name");
            Date createTime = rs.getDate("create_time");
            sourceContext.collect(new User(name, createTime));
        }
    }

    @Override
    public void cancel() {

    }

    @Override
    public void close() throws Exception {
        DbUtils.close(connection, stmt);
    }
}
