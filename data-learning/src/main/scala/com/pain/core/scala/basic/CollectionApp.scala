package com.pain.core.scala.basic

import com.pain.support.JsonUtil

import java.util
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import collection.JavaConverters._
import scala.collection.JavaConversions._

object CollectionApp {
    def main(args: Array[String]): Unit = {

        println("=== array")
        testArray()

        println("=== list")
        testList()

        println(sum(10, 20, 30))

        val tagCountStat = new util.ArrayList[util.Map[String, Any]]()
        val detail = new util.HashMap[String, Any]()
        tagCountStat.add(Map[String, Any]("tagId" -> 1111, "count" -> 10000).asJava)
        val i = new util.HashMap[String, Any]()
        i.put("tagId", 1111)
        i.put("count", 10000)
        val map = Map[String, Any]("tagId" -> 1111, "count" -> 10000)
        detail.put("tags", tagCountStat)
        println(JsonUtil.objToStr(tagCountStat))
        println(JsonUtil.objToStr(i))
        println(map.get("tagId"))
    }

    def testArray(): Unit = {
        val players = Array("durant", "taylor", "lorry", "james")
        println(players(0))
        println(players.mkString("<", ",", ">"))

        for (player <- players) {
            printf(s"${player} ")
        }

        players.foreach(println)
        players.reverse.foreach(println)

        val mvpPlayers = ArrayBuffer[String]()
        mvpPlayers += "durant"
        mvpPlayers += "james"
        mvpPlayers += ("lorry", "wade")
        mvpPlayers ++= Array("taylor", "jordan")
        mvpPlayers.insert(0, "curry")
        mvpPlayers.remove(1)
        println(s"mvpPlayers: $mvpPlayers")
    }

    def testList(): Unit = {
        val lines = List("spark streaming", "kafka streaming", "kafka spark", "spark hbase", "spark hive", "spark sql")
        var kafkaLines = ListBuffer[String]()
        kafkaLines += "kafka streaming"
        kafkaLines += ("kafka spark", "kafka hive")

        println(s"lines: $lines")
        println(s"kafkaLines: $kafkaLines")
    }

    def sum(nums: Int*): Int = {
        if (nums.isEmpty) {
            0
        } else {
            nums.head + sum(nums.tail: _*)
        }
    }
}
