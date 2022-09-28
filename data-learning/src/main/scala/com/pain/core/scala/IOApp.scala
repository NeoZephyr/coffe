package com.pain.core.scala

import scala.io.Source

object IOApp {
    def main(args: Array[String]): Unit = {
        // testFile()
        testURL()
    }

    def testFile(): Unit = {
        val file = Source.fromFile("/Users/pain/Documents/bigdata/scala/scala-learning/input/article.txt")(scala.io.Codec.UTF8)

        for (line <- file.getLines()) {
            println(line)
        }
    }

    def testURL(): Unit = {
        val file = Source.fromURL("http://www.baidu.com")(scala.io.Codec.UTF8)

        for (line <- file.getLines()) {
            println(line)
        }
    }
}
