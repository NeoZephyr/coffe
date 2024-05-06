package jubi.netty;

import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jubi.netty.client.TransportClient;
import jubi.netty.handler.*;

public class TransportContext {

    private static final MessageCodec CODEC = new MessageCodec();

    private TransportConf transportConf;

    public TransportContext(TransportConf transportConf) {
        this.transportConf = transportConf;
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