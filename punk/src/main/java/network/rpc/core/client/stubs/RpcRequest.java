package network.rpc.core.client.stubs;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class RpcRequest {
    private final String serviceName;
    private final String methodName;
    private final byte[] args;
}