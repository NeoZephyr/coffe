package com.pain.core.hadoop.stat.province

import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.Reducer

import java.{lang, util}

class ProvinceStatReducer extends Reducer[Text, LongWritable, Text, LongWritable] {

    private val outputValue = new LongWritable()

    override def reduce(key: Text, values: lang.Iterable[LongWritable], context: Reducer[Text, LongWritable, Text, LongWritable]#Context): Unit = {
        val iterator: util.Iterator[LongWritable] = values.iterator()
        var count: Long = 0

        while (iterator.hasNext) {
            count += iterator.next().get()
        }

        outputValue.set(count)
        context.write(key, outputValue)
    }
}
