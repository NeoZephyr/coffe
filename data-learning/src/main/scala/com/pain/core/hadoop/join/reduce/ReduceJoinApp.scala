package com.pain.core.hadoop.join.reduce

import com.pain.core.hadoop.join.DataInfo
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{NullWritable, Text}
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.{MultipleInputs, TextInputFormat}
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat

import java.io.File
import scala.reflect.io.Directory

object ReduceJoinApp {

    val HDFS_URL = "hdfs://cdh:8020"

    def main(args: Array[String]): Unit = {
        if (args.length < 3) {
            throw new IllegalArgumentException("must provide input path and output path")
        }

        // System.setProperty("HADOOP_USER_NAME", "vagrant")
        val configuration = new Configuration()
        // configuration.set("fs.defaultFs", HDFS_URL)
        val job: Job = Job.getInstance(configuration)
        job.setJarByClass(ReduceJoinApp.getClass)
        job.setMapperClass(classOf[ReduceJoinMapper])
        job.setReducerClass(classOf[ReduceJoinReducer])
        job.setMapOutputKeyClass(classOf[Text])
        job.setMapOutputValueClass(classOf[DataInfo])
        job.setOutputKeyClass(classOf[Text])
        job.setOutputValueClass(classOf[NullWritable])

        val outputFilePath = new Path(args(2))
        new Directory(new File(args(2))).deleteRecursively()

        MultipleInputs.addInputPath(job, new Path(args(0)), classOf[TextInputFormat])
        MultipleInputs.addInputPath(job, new Path(args(1)), classOf[TextInputFormat])
        FileOutputFormat.setOutputPath(job, outputFilePath)

        val result: Boolean = job.waitForCompletion(true)

        System.exit(if (result) 0 else 1)
    }
}
