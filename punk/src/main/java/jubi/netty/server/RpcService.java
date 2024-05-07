package jubi.netty.server;

public abstract class RpcService {
    public abstract void receiveAndReply(Object content);
    public void onStart() {};
    public void onStop() {};
}