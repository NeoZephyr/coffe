package net.grpc;

import io.grpc.*;

import java.util.Random;

public class HeaderEnhanceInterceptor implements ClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(ClientCall.Listener<RespT> responseListener, Metadata headers) {
                String methodName = method.getBareMethodName();

                if (!"Ping".equals(methodName)) {
                    headers.put(RpcMetadataKey.TRACE_ID_KEY, RpcContextKey.TRACE_ID_KEY.get());
                }

                headers.put(RpcMetadataKey.REQUEST_ID_KEY, RpcContextKey.REQUEST_ID_KEY.get());

                int num = new Random().nextInt(10);

                super.start(responseListener, headers);
            }
        };
    }
}
