package pipe.service;

public interface ProbeService {
    String ping();
    String ready(String component);
}
