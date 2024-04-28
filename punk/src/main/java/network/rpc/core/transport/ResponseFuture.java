package network.rpc.core.transport;

import lombok.Data;
import network.rpc.core.transport.command.Command;

import java.util.concurrent.CompletableFuture;

@Data
public class ResponseFuture {

    private final int requestId;
    private final CompletableFuture<Command> future;
    private final long timestamp;

    public ResponseFuture(int requestId, CompletableFuture<Command> future) {
        this.requestId = requestId;
        this.future = future;
        timestamp = System.nanoTime();
    }
}