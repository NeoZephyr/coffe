package net.grpc;

import com.pain.flame.grpc.PingRequest;
import com.pain.flame.grpc.PingResponse;
import com.pain.flame.grpc.PunkGrpc;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class PunkService extends PunkGrpc.PunkImplBase {

    @SneakyThrows
    @Override
    public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
        int second = new Random().nextInt(5);

        log.info("ping. sleep {}", second);
        Thread.sleep(second * 1000);
        PingResponse response = PingResponse.newBuilder()
                .setSuccess(true)
                .setMessage(String.format("pong %d seconds", second))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
