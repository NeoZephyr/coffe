package pipe.service;

public class DefaultProbeService implements ProbeService {

    @Override
    public String ping() {
        return "pong";
    }

    @Override
    public String ready(String component) {
        return String.format("%s is ready!", component);
    }
}
