package net.grpc;

import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.time.Duration;

@Slf4j
public class RetryInterceptor implements ClientInterceptor {

    private RetryStrategy retryStrategy;

    public RetryInterceptor(RetryStrategy retryStrategy) {
        this.retryStrategy = retryStrategy;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
                                                               CallOptions callOptions,
                                                               Channel next) {

        class RetryUnaryRequestClientCall<ReqT, RespT> extends ClientCall<ReqT, RespT> {

            Listener listener;
            Metadata metadata;
            ReqT message;
            int messageCount;
            ClientCall call;
            long start;
            int retryTimes;

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                this.listener = responseListener;
                this.metadata = headers;

                if (start == 0) {
                    this.start = System.currentTimeMillis();
                }
            }

            @Override
            public void request(int numMessages) {
                messageCount += numMessages;
            }

            @Override
            public void cancel(@Nullable String message, @Nullable Throwable cause) {
                if (call != null) {
                    call.cancel(message, cause);
                }

                listener.onClose(Status.CANCELLED.withDescription(message).withCause(cause), new Metadata());
            }

            @Override
            public void halfClose() {
                startCall(new CheckingListener());
            }

            @Override
            public void sendMessage(ReqT message) {
                this.message = message;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            private void startCall(Listener listener) {
                call = next.newCall(method, callOptions);
                Metadata headers = new Metadata();
                headers.merge(metadata);
                call.start(listener, headers);
                call.request(messageCount);
                call.sendMessage(message);
                call.halfClose();
            }

            class CheckingListener extends ForwardingClientCallListener {
                Listener<RespT> delegate;

                @Override
                protected Listener delegate() {
                    if (delegate == null) {
                        throw new IllegalStateException();
                    }

                    return delegate;
                }

                @Override
                public void onHeaders(Metadata headers) {
                    delegate = listener;
                    super.onHeaders(headers);
                }

                @Override
                public void onClose(Status status, Metadata trailers) {
                    if (delegate != null) {
                        super.onClose(status, trailers);
                        return;
                    }

                    log.warn("rpc of {} retry times: {}", method.getFullMethodName(), retryTimes);

                    Duration retryDelay = retryStrategy.getRetryDelay(
                            RetryFactor.builder()
                                    .retryTimes(retryTimes)
                                    .method(method.getFullMethodName())
                                    .status(status.getCode().name())
                                    .build());

                    if (retryDelay.isZero()) {
                        log.warn("finish rpc retry of {}, cost: {}", method.getFullMethodName(), (System.currentTimeMillis() - start) / 1000);
                        delegate = listener;
                        super.onClose(status, trailers);
                        return;
                    }

                    retryTimes += 1;
                    Uninterruptibles.sleepUninterruptibly(retryDelay);
                    startCall(new CheckingListener());
                }
            }
        }
        return new RetryUnaryRequestClientCall<>();
    }
}
