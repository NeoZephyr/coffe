package net.tcp.service;

public interface ProbeService {
    String ping();
    String ready(String component);
    String status();
}
