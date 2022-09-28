package com.pain.core.scala.basic

object FunctionApp {

    def main(args: Array[String]): Unit = {

        println(s"max(5, 10) = ${max(5, 10)}")
        println(s"sum(1, 2, 3, 4, 5) = ${sum(1, 2, 3, 4, 5)}")

        hello()

        val helloFunc = getHelloFunc()
        helloFunc("jack")

        val lines = List("spark hive", "spark hbase", "spark kafka")
        println("=== map")
        lines.flatMap((line: String) => line.split(" ")).foreach(println)
        println("=== filter")
        lines.filter((line: String) => line.contains("kafka")).foreach(println)
        println("=== group by")
        lines.flatMap((line: String) => line.split(" "))
            .groupBy((word: String) => word)
            .map(e => (e._1, e._2.size))
            .toList.sortWith((left, right) => {
            left._2 < right._2
        })
            .foreach(println)

        val nums = List(1, 2, 3, 4, 5)
        println(nums.reduce((x: Int, y: Int) => x - y))
        println(nums.fold(100)((x: Int, y: Int) => x + y))

        println(s"type: ${getObjType(3.14)}")
    }

    def max(x: Int, y: Int): Int = {
        if (x > y) {
            x
        } else {
            y
        }
    }

    def sum(nums: Int*): Int = {
        var result = 0
        for (elem <- nums) {
            result += elem
        }

        result
    }

    def hello(name: String = "pain"): Unit = {
        println(s"hello, ${name}")
    }

    def getHelloFunc() = {
        hello _
    }

    /**
     * 偏函数：被包含在花括号内没有 match 的一组 case
     */
    def getObjType: PartialFunction[Any, String] = {
        case _: Int => "Int"
        case _: String => "String"
        case _ => "Any"
    }

}
