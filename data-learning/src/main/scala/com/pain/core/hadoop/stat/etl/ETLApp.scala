package com.pain.core.hadoop.stat.etl

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.{NullWritable, Text}
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat

import java.net.URI

object ETLApp {

    val HDFS_URL = "hdfs://cdh:8020"

    def main(args: Array[String]): Unit = {
        // hdfs://cdh:8020/hdfs_test/stat/input hdfs://cdh:8020/hdfs_test/stat/etl
        if (args.length < 2) {
            throw new IllegalArgumentException("must provide input path and output path")
        }

        System.setProperty("HADOOP_USER_NAME", "vagrant")
        val configuration = new Configuration()
        configuration.set("fs.defaultFs", HDFS_URL)
        val job: Job = Job.getInstance(configuration)
        job.setJarByClass(ETLApp.getClass)
        job.setMapperClass(classOf[ETLMapper])
        job.setMapOutputKeyClass(classOf[NullWritable])
        job.setMapOutputValueClass(classOf[Text])

        val fileSystem: FileSystem = FileSystem.get(new URI(HDFS_URL), configuration, "vagrant")
        val outputFilePath = new Path(args(1))

        if (fileSystem.exists(outputFilePath)) {
            fileSystem.delete(outputFilePath, true)
        }

        // hdfs://cdh:8020 加前缀，不然是本地运行的
        FileInputFormat.setInputPaths(job, new Path(args(0)))
        FileOutputFormat.setOutputPath(job, outputFilePath)

        val result: Boolean = job.waitForCompletion(true)

        System.exit(if (result) 0 else 1)
    }
}
