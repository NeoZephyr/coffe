package com.pain.ink.function;

import org.apache.flink.api.common.functions.Partitioner;

public class AccessPartitioner implements Partitioner<String> {

    @Override
    public int partition(String s, int i) {
        if ("www.baidu.com".equals(s)) {
            return 0;
        } else if ("www.google.com".equals(s)) {
            return 1;
        }

        return 2;
    }
}
