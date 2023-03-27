package jubi.service;

import jubi.config.JubiConf;

public interface Service {

    void initialize(JubiConf conf);

    void start();

    void stop();

    String getName();

    Long getStartTime();

    JubiConf getConf();

    ServiceState getState();
}
