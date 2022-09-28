package com.pain.flame.punk.service;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RouteDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> key = new ThreadLocal<String>();

    @Override
    protected Object determineCurrentLookupKey() {
        return key.get();
    }

    public static void setDataSource(String dataSource) {
        key.set(dataSource);
    }

    public static String getDataSource() {
        return key.get();
    }

    public static void clearDataSource() {
        key.remove();
    }
}
