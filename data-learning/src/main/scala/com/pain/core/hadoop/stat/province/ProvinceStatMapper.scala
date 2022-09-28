package com.pain.core.hadoop.stat.province

import org.apache.commons.lang3.StringUtils
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.Mapper

class ProvinceStatMapper extends Mapper[LongWritable, Text, Text, LongWritable] {

    private val outputKey = new Text()
    private val outputValue = new LongWritable(1)

    override def map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, Text, LongWritable]#Context): Unit = {
        val items: Array[String] = value.toString.split("\t")

        if (items.length > 2 && !StringUtils.isBlank(items(2))) {
            outputKey.set(items(2))
        } else {
            outputKey.set("-")
        }

        context.write(outputKey, outputValue)
    }
}
