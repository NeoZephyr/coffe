package com.pain.core.hadoop.stat.etl

import com.pain.core.hadoop.common.{LogParser, UrlUtils}
import org.apache.commons.lang.StringUtils
import org.apache.hadoop.io.{LongWritable, NullWritable, Text}
import org.apache.hadoop.mapreduce.Mapper

import java.util

class ETLMapper extends Mapper[LongWritable, Text, NullWritable, Text] {

    private var logParser: LogParser = _

    override def setup(context: Mapper[LongWritable, Text, NullWritable, Text]#Context): Unit = {
        logParser = new LogParser
    }

    override def map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, NullWritable, Text]#Context): Unit = {
        val log: String = value.toString
        val logInfo: util.Map[String, String] = logParser.parse(log)

        val ip: String = logInfo.get("ip")
        val country: String = if (logInfo.get("country") == null) "-" else logInfo.get("country")
        val province: String = if (logInfo.get("province") == null) "-" else logInfo.get("province")
        val city: String = if (logInfo.get("city") == null) "-" else logInfo.get("city")
        val url: String = logInfo.get("url")
        val sessionId: String = logInfo.get("sessionId")
        val time: String = logInfo.get("time")
        val pageId: String = if (UrlUtils.getPageId(url) == "") "-" else UrlUtils.getPageId(url)

        val sb = new StringBuilder

        sb.append(ip).append("\t")
        sb.append(country).append("\t")
        sb.append(province).append("\t")
        sb.append(city).append("\t")
        sb.append(url).append("\t")
        sb.append(time).append("\t")
        sb.append(pageId).append("\t")
        sb.append(sessionId)

        if (StringUtils.isNotBlank(pageId) && !pageId.equals("-")) {
            System.out.println("------" + pageId)
        }

        context.write(NullWritable.get(), new Text(sb.toString()))
    }

}
