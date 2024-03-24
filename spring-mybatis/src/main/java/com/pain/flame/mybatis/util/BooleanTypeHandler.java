package com.pain.flame.mybatis.util;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanTypeHandler implements TypeHandler {

    public void setParameter(PreparedStatement preparedStatement, int i, Object o, JdbcType jdbcType) throws SQLException {
        if (o == null) {
            preparedStatement.setInt(i, 0);
        }

        Boolean value = (Boolean) o;
        preparedStatement.setInt(i, value ? 1: 0);
    }

    public Object getResult(ResultSet resultSet, String s) throws SQLException {
        int value = resultSet.getInt(s);

        if (value == 1) {
            return true;
        } else {
            return false;
        }
    }

    public Object getResult(ResultSet resultSet, int i) throws SQLException {
        return null;
    }

    public Object getResult(CallableStatement callableStatement, int i) throws SQLException {
        return null;
    }
}
