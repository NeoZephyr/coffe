package network.tcp.service;

public interface ProbeService {
    String ping();
    String ready(String component);
    String status();
}
