package jubi.netty.server;

import jubi.netty.RpcAddress;
import jubi.netty.client.RpcResponseCallback;
import jubi.netty.core.RpcEnv;

public class RpcCallContext {

    RpcEnv env;
    RpcResponseCallback callback;
    RpcAddress sender;

    public RpcCallContext(RpcEnv env, RpcResponseCallback callback, RpcAddress sender) {
        this.env = env;
        this.callback = callback;
        this.sender = sender;
    }

    public void reply(Object response) {
        send(response);
    }

    public void failure(Throwable e) {
        send(null);
    }

    private void send(Object message) {
        callback.onSuccess(null);
    }
}
