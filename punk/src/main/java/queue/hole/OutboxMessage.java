package queue.hole;

import lombok.extern.slf4j.Slf4j;
import network.rpc.core.transport.Transport;

import java.nio.ByteBuffer;

@Slf4j
public class OutboxMessage {

    ByteBuffer content;

    public OutboxMessage(ByteBuffer content) {
        this.content = content;
    }

    void sendWith(Transport transport) {
        transport.send(null);
    }
}
