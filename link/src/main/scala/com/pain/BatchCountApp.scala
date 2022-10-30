package com.pain

import org.apache.commons.io.FileUtils
import org.apache.flink.api.common.JobExecutionResult
import org.apache.flink.api.common.accumulators.LongCounter
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.common.operators.Order
import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}
import org.apache.flink.configuration.Configuration
import org.apache.flink.core.fs.FileSystem.WriteMode

import java.io.File
import java.nio.charset.StandardCharsets
import java.util
import scala.collection.mutable.ListBuffer

object BatchCountApp {
    def main(args: Array[String]): Unit = {
        val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment

        // readTextFile(env)
        // readCsvFile(env)
        // readCompressFile(env)
        // readRecursiveFile(env)

        // joinTest(env)
        // topN(env)

        // sink(env)

        // acc(env)
        cache(env)
    }

    def readTextFile(env: ExecutionEnvironment): Unit = {
        val dataset: DataSet[String] = env.readTextFile("input/data.txt")

        import org.apache.flink.api.scala._

        dataset.flatMap(_.toLowerCase.split("\\W+"))
            .filter(_.nonEmpty)
            .map((_, 1))
            .groupBy(0)
            .sum(1)
            .print()
    }

    def readCsvFile(env: ExecutionEnvironment): Unit = {
        import org.apache.flink.api.scala._

        // env.readCsvFile[(Int, String)]("input/data.csv", ignoreFirstLine = true, includedFields = Array(0, 1)).print()

        // case class
        // env.readCsvFile[Student]("input/data.csv", ignoreFirstLine = true, includedFields = Array(0, 3)).print()

        // pojo
        env.readCsvFile[Person]("input/data.csv", ignoreFirstLine = true, pojoFields = Array("name", "city")).print()
    }

    def readRecursiveFile(env: ExecutionEnvironment): Unit = {
        val configuration = new Configuration()
        configuration.setBoolean("recursive.file.enumeration", true)
        env.readTextFile("input/nested").withParameters(configuration).print()
    }

    def readCompressFile(env: ExecutionEnvironment): Unit = {
        env.readTextFile("input/compress.txt.gz").print()
    }

    def joinTest(env: ExecutionEnvironment): Unit = {
        val players: ListBuffer[(String, String)] = ListBuffer[(String, String)]()
        players.append(("Curry", "Warriors"))
        players.append(("Tompson", "Warriors"))
        players.append(("Durant", "Nets"))
        players.append(("Jimmy", "Heat"))

        val teams: ListBuffer[(String, String)] = ListBuffer[(String, String)]()
        teams.append(("Warriors", "GoldState"))
        teams.append(("Heat", "MaiaMi"))
        teams.append(("Rocket", "Houston"))

        import org.apache.flink.api.scala._

        val data1: DataSet[(String, String)] = env.fromCollection(players)
        val data2: DataSet[(String, String)] = env.fromCollection(teams)

        data1.join(data2).where(1).equalTo(0).apply((first, second) => {
            (first._1, first._2, second._2)
        }).print()

        data1.leftOuterJoin(data2).where(1).equalTo(0).apply((first, second) => {
            val city = if (second == null) "none" else second._2
            (first._1, first._2, city)
        }).print()

        data1.fullOuterJoin(data2).where(1).equalTo(0).apply((first, second) => {
            if (first == null) {
                ("none", second._1, second._2)
            } else if (second == null) {
                (first._1, first._2, "none")
            } else {
                (first._1, first._2, second._2)
            }
        }).print()

        data1.cross(data2).print()
    }

    def topN(env: ExecutionEnvironment): Unit = {
        val games: ListBuffer[(String, Int)] = ListBuffer[(String, Int)]()
        games.append(("Curry", 34))
        games.append(("Curry", 29))
        games.append(("Curry", 43))
        games.append(("Curry", 16))
        games.append(("Tompson", 19))
        games.append(("Durant", 34))
        games.append(("Jimmy", 47))
        games.append(("Green", 2))

        import org.apache.flink.api.scala._

        val data: DataSet[(String, Int)] = env.fromCollection(games)

        // data.first(3).print()
        // data.groupBy(0).first(3).print()
        data.groupBy(0).sortGroup(1, Order.ASCENDING).first(3).print()
    }

    def sink(env: ExecutionEnvironment): Unit = {
        val students: ListBuffer[(String, Int)] = ListBuffer[(String, Int)]()
        students.append(("Curry", 34))
        students.append(("Mills", 29))
        students.append(("Jhon", 29))
        students.append(("Smith", 29))
        students.append(("Durant", 29))
        students.append(("Kobe", 29))
        students.append(("TD", 29))

        import org.apache.flink.api.scala._

        val data: DataSet[(String, Int)] = env.fromCollection(students)
        data.writeAsCsv("output/student", "\n", " | ", WriteMode.OVERWRITE)
            .setParallelism(5)

        env.execute("sink")
    }

    def acc(env: ExecutionEnvironment): Unit = {
        import org.apache.flink.api.scala._

        val data: DataSet[String] = env.fromElements("spark", "hadoop", "flink", "storm", "hive", "hbase")

        data.map(new RichMapFunction[String, Long] {
            var counter = 0L

            override def map(in: String): Long = {
                counter = counter + 1
                counter
            }
        }).setParallelism(4).print()

        val mapData: DataSet[String] = data.map(new RichMapFunction[String, String] {

            val counter = new LongCounter()

            override def open(parameters: Configuration): Unit = {
                getRuntimeContext.addAccumulator("counter", counter)
            }

            override def map(in: String): String = {
                counter.add(1)
                in
            }
        })

        mapData.writeAsText("output/acc", WriteMode.OVERWRITE).setParallelism(5)
        val result: JobExecutionResult = env.execute("counter")

        val counter: Long = result.getAccumulatorResult[Long]("counter")

        println(s"counter: $counter")
    }

    def cache(env: ExecutionEnvironment): Unit = {
        env.registerCachedFile("input/data.txt", "cache")

        import org.apache.flink.api.scala._

        val data: DataSet[String] = env.fromElements("spark", "hadoop", "flink", "storm", "hive", "hbase")

        data.map(new RichMapFunction[String, String] {
            override def open(parameters: Configuration): Unit = {
                val file: File = getRuntimeContext.getDistributedCache.getFile("cache")
                val lines: util.List[String] = FileUtils.readLines(file, StandardCharsets.UTF_8)

                import scala.collection.JavaConverters._

                for (elem <- lines.asScala) {
                    println(elem)
                }
            }

            override def map(in: String): String = {
                in
            }
        }).print()
    }

    case class Student(age: Int, job: String)
}
