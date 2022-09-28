package com.pain.core.hadoop.join.map

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{NullWritable, Text}
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat

import java.io.File
import java.net.URI
import scala.reflect.io.Directory

object MapJoinApp {

    def main(args: Array[String]): Unit = {
        if (args.length < 3) {
            throw new IllegalArgumentException("must provide input path and output path")
        }

        val configuration = new Configuration()
        val job: Job = Job.getInstance(configuration)
        job.setJarByClass(MapJoinApp.getClass)
        job.setMapperClass(classOf[MapJoinMapper])
        job.setMapOutputKeyClass(classOf[Text])
        job.setMapOutputValueClass(classOf[NullWritable])
        job.setNumReduceTasks(0)
        job.addCacheFile(new URI(args(0)))

        new Directory(new File(args(2))).deleteRecursively()
        FileInputFormat.addInputPath(job, new Path(args(1)))
        FileOutputFormat.setOutputPath(job, new Path(args(2)))

        val result: Boolean = job.waitForCompletion(true)

        System.exit(if (result) 0 else 1)
    }
}
