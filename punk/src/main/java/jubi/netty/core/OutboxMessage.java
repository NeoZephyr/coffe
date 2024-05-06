package jubi.netty.core;

import jubi.netty.client.TransportClient;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
public class OutboxMessage {

    TransportClient client = null;
    Long requestId = null;
    ByteBuffer content;

    public OutboxMessage(ByteBuffer content) {
        this.content = content;
    }

    void sendWith(TransportClient client) {
        this.client = client;
        this.requestId = client.sendRpc(null, null);
    }
}
