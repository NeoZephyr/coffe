package network.rpc.core.transport;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.TimeoutException;

public interface ClientTransportFactory extends Closeable {

    Transport createTransport(SocketAddress address) throws InterruptedException, TimeoutException, IOException;

    @Override
    void close();
}