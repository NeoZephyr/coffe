package com.pain.core.hadoop.join.map

import org.apache.hadoop.io.{LongWritable, NullWritable, Text}
import org.apache.hadoop.mapreduce.Mapper

import java.io.File
import scala.collection.mutable
import scala.io.{BufferedSource, Source}

class MapJoinMapper extends Mapper[LongWritable, Text, Text, NullWritable] {
    private val outKey = new Text()
    private val cache = new mutable.HashMap[String, String]()

    override def setup(context: Mapper[LongWritable, Text, Text, NullWritable]#Context): Unit = {
        val path: String = context.getCacheFiles()(0).toString
        val source: BufferedSource = Source.fromFile(new File(path))
        source.getLines().foreach((line: String) => {
            val words: Array[String] = line.split("\\s+")
            // 1   Lakers   LosAngeles
            cache.put(words(0), words(1))
        })
    }

    override def map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, Text, NullWritable]#Context): Unit = {
        val words: Array[String] = value.toString.split("\\s+")
        val length: Int = words.length

        if (length == 4) {
            // 23    james    1    36
            cache.get(words(2)) match {
                case Some(team) =>
                    outKey.set(s"team: $team\tname: ${words(1)}\tno: ${words(0)}\tage: ${words(3)}")
                    context.write(outKey, NullWritable.get())
                case _ =>
            }
        }
    }
}
