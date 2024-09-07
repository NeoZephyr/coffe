package com.pain.ink.function;

import com.pain.ink.bean.Access;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;

import java.util.Date;
import java.util.Random;

public class AccessParallelSource implements ParallelSourceFunction<Access> {

    boolean running = true;

    String[] domains = {"www.baidu.com", "www.google.com", "www.ebay.com"};

    @Override
    public void run(SourceContext<Access> ctx) throws Exception {
        Random random = new Random();

        while (running) {
            for (int i = 0; i < 10; i++) {
                Access access = new Access(new Date().toString(), domains[random.nextInt(domains.length)], random.nextInt(1000));
                ctx.collect(access);
            }

            Thread.sleep(3000);
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
}
