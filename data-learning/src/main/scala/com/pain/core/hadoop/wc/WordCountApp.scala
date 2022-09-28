package com.pain.core.hadoop.wc

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.compress.{BZip2Codec, CompressionCodec}
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat

import java.net.URI

object WordCountApp {

    val HDFS_URL = "hdfs://cdh:8020"

    def main(args: Array[String]): Unit = {
        // hdfs://cdh:8020/hdfs_test/words.txt hdfs://cdh:8020/hdfs_test/output
        if (args.length < 2) {
            throw new IllegalArgumentException("must provide input path and output path")
        }

        System.setProperty("HADOOP_USER_NAME", "vagrant")
        val configuration = new Configuration()
        configuration.set("fs.defaultFs", HDFS_URL)

        // map 输出压缩
        configuration.setBoolean("mapreduce.map.output.compress", true)
        configuration.setClass("mapreduce.map.output.compress.codec", classOf[BZip2Codec], classOf[CompressionCodec])
        val job: Job = Job.getInstance(configuration)
        job.setJarByClass(WordCountApp.getClass)
        job.setMapperClass(classOf[WordCountMapper])
        job.setReducerClass(classOf[WordCountReducer])
        job.setCombinerClass(classOf[WordCountReducer])
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
