package network.rpc.core;

import network.rpc.api.RpcAccessPoint;
import network.rpc.api.ServiceSupport;
import network.rpc.core.client.StubFactory;
import network.rpc.core.server.ServiceProviderRegistry;
import network.rpc.core.transport.RequestHandlerRegistry;
import network.rpc.core.transport.Transport;
import network.rpc.core.transport.ClientTransportFactory;
import network.rpc.core.transport.TransportServer;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettyRpcAccessPoint implements RpcAccessPoint {

    private final String host = "localhost";
    private final int port = 9999;
    private final URI uri = URI.create("rpc://" + host + ":" + port);
    private TransportServer server = null;
    private ClientTransportFactory client = ServiceSupport.load(ClientTransportFactory.class);
    private Map<URI, Transport> transports = new ConcurrentHashMap<>();
    private final StubFactory stubFactory = ServiceSupport.load(StubFactory.class);
    private final ServiceProviderRegistry serviceProviderRegistry = ServiceSupport.load(ServiceProviderRegistry.class);

    @Override
    public <T> T getRemoteService(URI uri, Class<T> serviceClass) {
        Transport transport = transports.computeIfAbsent(uri, this::createTransport);
        return stubFactory.createStub(transport, serviceClass);
    }

    @Override
    public synchronized <T> URI addServiceProvider(T service, Class<T> serviceClass) {
        serviceProviderRegistry.addServiceProvider(serviceClass, service);
        return uri;
    }

    @Override
    public Closeable startServer() throws Exception {
        if (server == null) {
            server = ServiceSupport.load(TransportServer.class);
            server.start(RequestHandlerRegistry.getInstance(), port);
        }

        return () -> {
            if (server != null) {
                server.stop();
            }
        };
    }

    @Override
    public void close() throws IOException {
        if (server != null) {
            server.stop();
        }

        client.close();
    }

    private Transport createTransport(URI uri) {
        try {
            // todo cached
            // InetSocketAddress.createUnresolved(host, port);
            return client.createTransport(new InetSocketAddress(uri.getHost(), uri.getPort()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}