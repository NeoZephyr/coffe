package jubi.netty.core;

import jubi.netty.RpcAddress;
import jubi.netty.server.RpcCallContext;

public class InboxMessage {

    RpcAddress sender;
    Object content;
    RpcCallContext context;

    public InboxMessage() {}

    public InboxMessage(RpcAddress sender, Object content, RpcCallContext context) {
        this.sender = sender;
        this.content = content;
        this.context = context;
    }
}
