package com.pain.core.hadoop.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LogParser {
    private final Logger logger = LoggerFactory.getLogger(LogParser.class);

    public Map<String, String> parse(String log)  {
        Map<String, String> logInfo = new HashMap<>();
        IPParser ipParse = IPParser.getInstance();
        if(StringUtils.isNotBlank(log)) {
            String[] splits = log.split("\001");

            String ip = splits[13];
            String url = splits[1];
            String sessionId = splits[10];
            String time = splits[17];

            logInfo.put("ip",ip);
            logInfo.put("url",url);
            logInfo.put("sessionId",sessionId);
            logInfo.put("time",time);

            IPParser.RegionInfo regionInfo = ipParse.analyseIp(ip);

            logInfo.put("country",regionInfo.getCountry());
            logInfo.put("province",regionInfo.getProvince());
            logInfo.put("city",regionInfo.getCity());
        } else {
            logger.error("日志记录的格式不正确：" + log);
        }

        return logInfo;
    }
}
