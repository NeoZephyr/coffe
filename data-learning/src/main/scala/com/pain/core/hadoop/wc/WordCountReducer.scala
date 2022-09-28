package com.pain.core.hadoop.wc

import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.Reducer

import java.{lang, util}

/**
 *
 * 如果要处理自定义的类型，需要实现 Writable 接口。参考 LongWritable, Text 等
 *
 * 如果不需要输出 key，可以设置 key 为 NullWritable.get()
 *
 * Partitioner，默认分区规则为 HashPartitioner。使用分区，需要设置 numReduceTasks
 */
class WordCountReducer extends Reducer[Text, LongWritable, Text, LongWritable] {

    private val outValue = new LongWritable()

    override def reduce(key: Text, values: lang.Iterable[LongWritable], context: Reducer[Text, LongWritable, Text, LongWritable]#Context): Unit = {

        var sum: Long = 0

        val iterator: util.Iterator[LongWritable] = values.iterator()

        while (iterator.hasNext) {
            sum += iterator.next().get()
        }

        outValue.set(sum)
        context.write(key, outValue)
    }

}
