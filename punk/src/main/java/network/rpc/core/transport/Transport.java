package network.rpc.core.transport;

import network.rpc.core.transport.command.Command;

import java.util.concurrent.CompletableFuture;

public interface Transport {
    CompletableFuture<Command> send(Command request);
}
