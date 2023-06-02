package jubi.netty.handler;

import jubi.netty.protocol.Message;

public abstract class MessageProcessor<T extends Message> {

    public abstract void handle(T message) throws Exception;

    public abstract void channelActive();

    public abstract void exceptionCaught(Throwable cause);

    public abstract void channelInactive();
}