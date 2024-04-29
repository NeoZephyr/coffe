package network.rpc.core.client;

import network.rpc.core.transport.Transport;

public interface ServiceStub {
    void setTransport(Transport transport);
}
