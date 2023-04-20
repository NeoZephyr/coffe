package jubi.netty.client;

import jubi.netty.protocol.RpcResponse;

public interface RpcResponseCallback {

    void onSuccess(RpcResponse response);

    void onFailure(Throwable e);
}
