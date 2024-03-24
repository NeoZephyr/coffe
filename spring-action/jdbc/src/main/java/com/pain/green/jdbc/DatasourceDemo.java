package com.pain.green.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Date;

@Configuration
public class DatasourceDemo {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        // testRawConnection();
        testPoolConnection();
    }

    @Bean(destroyMethod = "close")
    public DataSource dataSource() throws Exception {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://cdh:3306/app?characterEncoding=utf-8&useSSL=false");
        config.setUsername("root");
        config.setPassword("123456");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        return new HikariDataSource(config);
    }

    @Bean
    public JdbcTemplate jdbcTemplate() throws Exception {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public PlatformTransactionManager transactionManager() throws Exception {
        return new DataSourceTransactionManager(dataSource());
    }

    private static void testPoolConnection() throws SQLException {
        ApplicationContext context = new ClassPathXmlApplicationContext("datasource.xml");
        DataSource dataSource = context.getBean(DataSource.class);
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("select * from customer where id = ?");
        statement.setInt(1, 1);
        ResultSet resultSet = statement.executeQuery();
        printResultSet(resultSet);
        resultSet.close();
        statement.close();
        connection.close();
    }

    private static void testRawConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://cdh:3306/app?characterEncoding=utf-8&useSSL=false", "root", "123456");
        Statement statement = connection.createStatement();
        String sql = "select * from customer where id = 1";
        ResultSet resultSet = statement.executeQuery(sql);
        printResultSet(resultSet);
        resultSet.close();
        statement.close();
        connection.close();
    }

    private static void printResultSet(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            String name = resultSet.getString("name");
            Date createTime = resultSet.getTimestamp("date_create");
            System.out.println("name: " + name);
            System.out.println("dateCreate: " + createTime);
        }
    }
}
