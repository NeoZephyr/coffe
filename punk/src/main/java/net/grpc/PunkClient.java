package net.grpc;

import com.pain.flame.grpc.PingRequest;
import com.pain.flame.grpc.PunkGrpc;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
public class PunkClient extends GrpcClient {

    private PunkGrpc.PunkBlockingStub stub;

    public PunkClient(String host, int port, boolean usePlaintext, int maxRetryAttempts) {
        super(host, port, usePlaintext, maxRetryAttempts, null);
        stub = PunkGrpc.newBlockingStub(channel).withDeadlineAfter(10, TimeUnit.SECONDS);
    }

    public PunkClient(String host, int port, boolean usePlaintext, int maxRetryAttempts, ClientInterceptor[] interceptors) {
        super(host, port, usePlaintext, maxRetryAttempts, interceptors);
        stub = PunkGrpc.newBlockingStub(channel).withDeadlineAfter(10, TimeUnit.SECONDS);
    }

    public void ping() throws Exception {
        Context ctx = Context.current();
        ctx = ctx.withValue(RpcContextKey.TRACE_ID_KEY, "110");
        ctx = ctx.withValue(RpcContextKey.REQUEST_ID_KEY, UUID.randomUUID().toString().replace("-", ""));

        PingRequest request = PingRequest.newBuilder().build();
        ctx.call(() -> sendRequest(stub -> stub.ping(request)));
    }

    <R> R sendRequest(Function<PunkGrpc.PunkBlockingStub, R> func) {
        try {
            return func.apply(stub);
        } catch (Exception ex) {
            log.warn("request exception", ex);
            throw ex;
        }
    }

    public static void main(String[] args) throws Exception {
        HeaderEnhanceInterceptor interceptor = new HeaderEnhanceInterceptor();
        PunkClient client = new PunkClient("localhost", 5858, true, 3, new HeaderEnhanceInterceptor[]{interceptor});
        client.ping();
    }
}
