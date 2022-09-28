package com.pain.core.presto

import java.sql.{Connection, DriverManager, ResultSet, Statement}

object ApiApp {
    def main(args: Array[String]): Unit = {
        Class.forName("com.facebook.presto.jdbc.PrestoDriver")
        val connection: Connection = DriverManager.getConnection("jdbc:presto://cdh:8012/hive/test_db", "vagrant", "")
        val statement: Statement = connection.createStatement()

        val resultSet: ResultSet = statement.executeQuery("select * from hive.test_db.player")

        while (resultSet.next()) {
            val name: String = resultSet.getString("name")
            println(s"name: $name")
        }
    }
}
