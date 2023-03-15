package net.grpc;

import io.grpc.Context;

public interface RpcContextKey {
    Context.Key<String> TRACE_ID_KEY = Context.key("traceId");
    Context.Key<String> REQUEST_ID_KEY = Context.key("requestId");
}
