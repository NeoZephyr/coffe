package com.pain.ink.function;

import com.pain.ink.bean.Access;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.configuration.Configuration;

public class AccessRichMap extends RichMapFunction<String, Access> {

    // 生命周期方法，初始化操作
    // 每个分区执行一次
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        System.out.println("=== open ===");
    }

    @Override
    public Access map(String s) throws Exception {
        String[] items = s.split(",");
        return new Access(items[0], items[1], Integer.parseInt(items[2]));
    }

    @Override
    public void close() throws Exception {
        super.close();
        System.out.println("=== close ===");
    }

    @Override
    public RuntimeContext getRuntimeContext() {
        return super.getRuntimeContext();
    }
}
