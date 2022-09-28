package com.pain.core.hadoop.join.reduce

import com.pain.core.hadoop.join.DataInfo
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.Mapper

class ReduceJoinMapper extends Mapper[LongWritable, Text, Text, DataInfo] {

    private val outKey = new Text()
    private val outValue = new DataInfo()

    override def map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, Text, DataInfo]#Context): Unit = {
        val words: Array[String] = value.toString.split("\\s+")
        val length: Int = words.length

        if (length == 3) {
            // 1   Lakers   LosAngeles
            outValue.flag = "t"
            outValue.data = words(1)
            outKey.set(words(0))
            context.write(outKey, outValue)
        }
        if (length == 4) {
            // 23    james    1    36
            outValue.flag = "p"
            outValue.data = s"name: ${words(1)}\tno: ${words(0)}\tage: ${words(3)}"
            outKey.set(words(2))
            context.write(outKey, outValue)
        }
    }
}
