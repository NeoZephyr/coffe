package network.rpc.core.transport;

public interface TransportServer {

    void start(RequestHandlerRegistry registry, int port) throws Exception;

    void stop();
}