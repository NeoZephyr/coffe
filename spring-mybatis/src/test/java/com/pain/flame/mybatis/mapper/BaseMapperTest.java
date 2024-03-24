package com.pain.flame.mybatis.mapper;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.testng.annotations.BeforeClass;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by Administrator on 2018/9/9.
 */
public class BaseMapperTest {
    private static SqlSessionFactory sqlSessionFactory;

    @BeforeClass
    public void setUp() {
        String resource = "mybatis-config.xml";

        try (Reader reader = Resources.getResourceAsReader(resource)) {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected SqlSession getSqlSession() {
        return sqlSessionFactory.openSession();
    }

    protected SqlSession getBatchSqlSession() {
        return sqlSessionFactory.openSession(ExecutorType.BATCH);
    }
}