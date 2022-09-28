package com.pain.core.hadoop.stat.province

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat

import java.net.URI

object ProvinceStatApp {

    val HDFS_URL = "hdfs://cdh:8020"

    def main(args: Array[String]): Unit = {
        // hdfs://cdh:8020/hdfs_test/stat/etl/trackinfo.txt hdfs://cdh:8020/hdfs_test/stat/province
        if (args.length < 2) {
            throw new IllegalArgumentException("must provide input path and output path")
        }

        System.setProperty("HADOOP_USER_NAME", "vagrant")
        val configuration = new Configuration()
        configuration.set("fs.defaultFs", HDFS_URL)
        val job: Job = Job.getInstance(configuration)
        job.setJarByClass(ProvinceStatApp.getClass)
        job.setMapperClass(classOf[ProvinceStatMapper])
        job.setReducerClass(classOf[ProvinceStatReducer])
        job.setMapOutputKeyClass(classOf[Text])
        job.setMapOutputValueClass(classOf[LongWritable])
        job.setOutputKeyClass(classOf[Text])
        job.setOutputValueClass(classOf[LongWritable])

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
