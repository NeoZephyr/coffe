package jubi.netty.client;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import jubi.netty.handler.MessageEncoder;
import jubi.netty.handler.TransportResponseHandler;

public class TransportContext {

    private TransportConf transportConf;

    private static final MessageEncoder ENCODER = MessageEncoder.INSTANCE;

    public TransportContext(TransportConf transportConf) {
        this.transportConf = transportConf;
    }

    public TransportClientFactory createClientFactory() {
        return new TransportClientFactory(this);
    }

    public TransportResponseHandler initializePipeline(
            SocketChannel channel, ChannelInboundHandlerAdapter decoder) {
        TransportResponseHandler responseHandler = new TransportResponseHandler(channel);
        channel
                .pipeline()
                .addLast("encoder", ENCODER) // out
                .addLast("decoder", decoder) // in
                .addLast(
                        "idleStateHandler", new IdleStateHandler(0, 0, transportConf.connectionActiveTimeoutMs() / 1000))
                .addLast("responseHandler", responseHandler);
        return responseHandler;
    }

    public TransportConf getConf() {
        return transportConf;
    }
}
