package network.grpc;

import com.pain.flame.grpc.PingRequest;
import com.pain.flame.grpc.PingResponse;
import com.pain.flame.grpc.PunkGrpc;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
public class PunkClient extends GrpcClient {

    private PunkGrpc.PunkBlockingStub stub;

    public PunkClient(String host, int port, boolean usePlaintext, int maxRetryAttempts) {
        super(host, port, usePlaintext, maxRetryAttempts, null);
        stub = PunkGrpc.newBlockingStub(channel);
    }

    public PunkClient(String host, int port, boolean usePlaintext, int maxRetryAttempts, ClientInterceptor[] interceptors) {
        super(host, port, usePlaintext, maxRetryAttempts, interceptors);
        stub = PunkGrpc.newBlockingStub(channel);
    }

    public void ping() throws Exception {
        Context ctx = Context.current();
        ctx = ctx.withValue(RpcContextKey.TRACE_ID_KEY, "110");
        ctx = ctx.withValue(RpcContextKey.REQUEST_ID_KEY, UUID.randomUUID().toString().replace("-", ""));

        PingRequest request = PingRequest.newBuilder().build();
        PingResponse response = ctx.call(() -> sendRequest(stub -> stub.ping(request)));

        log.info("response: {}", response.getMessage());
    }

    <R> R sendRequest(Function<PunkGrpc.PunkBlockingStub, R> func) {
        return func.apply(stub.withDeadlineAfter(3, TimeUnit.MINUTES));
    }

    public static void main(String[] args) throws Exception {
        HeaderEnhanceInterceptor interceptor = new HeaderEnhanceInterceptor();
        // PunkClient client = new PunkClient("localhost", 5858, true, 3, new HeaderEnhanceInterceptor[]{interceptor});
        PunkClient client = new PunkClient("localhost", 5858, true, 3);

        client.ping();

//        ExecutorService executorService = Executors.newFixedThreadPool(3);
//
//        for (int i = 0; i < 100; i++) {
//            int finalI = i;
//            executorService.submit(() -> {
//                try {
//                    client.ping();
//                } catch (StatusRuntimeException e) {
//                    log.warn("counter: {}, status: {}, message: {}", finalI, e.getStatus(), e.getMessage());
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            });
//        }
//
//        executorService.shutdown();
//        executorService.awaitTermination(1000, TimeUnit.SECONDS);
    }
}
