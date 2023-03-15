package net.grpc;

import io.grpc.Metadata;

public interface RpcMetadataKey {
    Metadata.Key<String> TRACE_ID_KEY = Metadata.Key.of("traceId", Metadata.ASCII_STRING_MARSHALLER);
    Metadata.Key<String> REQUEST_ID_KEY = Metadata.Key.of("requestId", Metadata.ASCII_STRING_MARSHALLER);
}
