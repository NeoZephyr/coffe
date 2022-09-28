package com.pain.core.hadoop.wc

import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.Mapper

class WordCountMapper extends Mapper[LongWritable, Text, Text, LongWritable] {

    private val outKey = new Text()
    private val outValue = new LongWritable()

    override def map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, Text, LongWritable]#Context): Unit = {

        val line: String = value.toString
        val words: Array[String] = line.split(" ")

        for (word <- words) {
            outKey.set(word)
            outValue.set(1)
            context.write(outKey, outValue)
        }
    }
}
