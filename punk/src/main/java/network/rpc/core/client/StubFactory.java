package network.rpc.core.client;

import network.rpc.core.transport.Transport;

public interface StubFactory {
    <T> T createStub(Transport transport, Class<T> serviceClass);
}
