package network.rpc.core.transport;

import network.rpc.core.transport.command.Command;

import java.io.Closeable;
import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;

public interface Transport extends Closeable {
    CompletableFuture<Command> send(Command request);

    boolean isActive();

    SocketAddress getAddress();
}
