package jubi.netty;

import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jubi.netty.client.TransportClient;
import jubi.netty.client.TransportClientFactory;
import jubi.netty.handler.*;
import jubi.netty.server.TransportServer;

public class TransportContext {

    private static final MessageCodec CODEC = new MessageCodec();

    private TransportConf transportConf;

    public TransportContext(TransportConf transportConf) {
        this.transportConf = transportConf;
    }

    public TransportClientFactory createClientFactory() {
        return new TransportClientFactory(this);
    }

    public TransportServer createServer(String host, int port) {
        return new TransportServer(this, null, port);
    }

    public TransportServer createServer(int port) {
        return new TransportServer(this, null, port);
    }

    public TransportServer createServer() {
        return createServer(0);
    }

    public TransportChannelHandler initializePipeline(SocketChannel channel) {
        TransportResponseProcessor responseProcessor = new TransportResponseProcessor(channel);
        TransportClient client = new TransportClient(channel, responseProcessor);
        TransportRequestProcessor requestProcessor = new TransportRequestProcessor(channel, client);
        TransportChannelHandler channelHandler = new TransportChannelHandler(
                client,
                requestProcessor,
                responseProcessor,
                transportConf.connectionActiveTimeoutMs(),
                transportConf.closeIdleConnections()
        );
        IdleStateHandler idleStateHandler = new IdleStateHandler(0, 0, transportConf.connectionActiveTimeoutMs() / 1000);
        channel
                .pipeline()
                .addLast("loggingHandler", new LoggingHandler(LogLevel.DEBUG))
                .addLast("frameDecoder", new TransportFrameDecoder())
                .addLast("codec", CODEC)
                .addLast("idleStateHandler", idleStateHandler)
                .addLast("handler", channelHandler);
        return channelHandler;
    }

    public TransportConf getConf() {
        return transportConf;
    }
}