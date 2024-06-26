package network.tcp.service;

public class DefaultProbeService implements ProbeService {

    @Override
    public String ping() {
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "pong";
    }

    @Override
    public String ready(String component) {
        return String.format("%s is ready!", component);
    }

    @Override
    public String status() {
        throw new UnsupportedOperationException("not support status probe");
    }
}
