package network.grpc;

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class GrpcClient {
    protected String host;
    protected int port;
    protected boolean usePlaintext;
    protected int maxRetryAttempts;
    protected ManagedChannel channel;

    protected GrpcClient(String host, int port, boolean usePlaintext, int maxRetryAttempts, ClientInterceptor[] interceptors) {
        this.host = host;
        this.port = port;
        this.usePlaintext = usePlaintext;
        this.maxRetryAttempts = maxRetryAttempts;

        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress(host, port);

        if (usePlaintext) {
            channelBuilder.usePlaintext();
        }

        if (maxRetryAttempts > 0) {
            channelBuilder.enableRetry().maxRetryAttempts(maxRetryAttempts);
        }

        if (interceptors != null && interceptors.length > 0) {
            channelBuilder.intercept(interceptors);
        }

        channelBuilder.maxInboundMessageSize(Integer.MAX_VALUE);
        channel = channelBuilder.build();
    }

    protected GrpcClient(ManagedChannel channel) {
        this.channel = channel;
    }

    public void close() {
        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Can not close GRPC client to {}:{}", host, port);
        }
    }
}