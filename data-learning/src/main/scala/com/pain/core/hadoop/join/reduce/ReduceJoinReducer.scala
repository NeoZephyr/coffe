package com.pain.core.hadoop.join.reduce

import com.pain.core.hadoop.join.DataInfo
import org.apache.hadoop.io.{NullWritable, Text}
import org.apache.hadoop.mapreduce.Reducer

import java.{lang, util}
import scala.collection.JavaConversions._

class ReduceJoinReducer extends Reducer[Text, DataInfo, Text, NullWritable] {

    override def reduce(key: Text, values: lang.Iterable[DataInfo], context: Reducer[Text, DataInfo, Text, NullWritable]#Context): Unit = {
        val iterator: util.Iterator[DataInfo] = values.iterator()
        val teams = new util.ArrayList[String]()
        val players = new util.ArrayList[String]()

        while (iterator.hasNext) {
            val dataInfo: DataInfo = iterator.next()
            if (dataInfo.flag == "t") {
                teams.add(dataInfo.data)
            } else {
                players.add(dataInfo.data)
            }
        }

        teams.foreach((team: String) => {
            players.foreach((player: String) => {
                context.write(new Text(s"team: $team\t$player"), NullWritable.get())
            })
        })
    }

}
