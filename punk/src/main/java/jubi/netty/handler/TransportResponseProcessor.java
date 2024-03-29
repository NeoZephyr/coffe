package jubi.netty.handler;

import io.netty.channel.Channel;
import jubi.netty.client.RpcResponseCallback;
import jubi.netty.protocol.ResponseMessage;
import jubi.netty.protocol.RpcFailure;
import jubi.netty.protocol.RpcResponse;
import jubi.netty.util.NettyUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class TransportResponseProcessor extends MessageProcessor<ResponseMessage> {

    private Channel channel;
    private Map<Long, RpcResponseCallback> rpcCallbacks;

    /** Records the time that the last RPC request was sent. */
    private AtomicLong lastRequestTime;

    public TransportResponseProcessor(Channel channel) {
        this.channel = channel;
        this.rpcCallbacks = new ConcurrentHashMap<>();
        this.lastRequestTime = new AtomicLong(0);
    }

    public void addRpcCallback(long requestId, RpcResponseCallback callback) {
        updateLastRequestTime();
        rpcCallbacks.put(requestId, callback);
    }

    public void removeRpcCallback(long requestId) {
        rpcCallbacks.remove(requestId);
    }

    public void updateLastRequestTime() {
        lastRequestTime.set(System.currentTimeMillis());
    }

    public long getLastRequestTime() {
        return lastRequestTime.get();
    }

    public boolean hasOutstandingRequests() {
        return !rpcCallbacks.isEmpty();
    }

    @Override
    public void handle(ResponseMessage message) throws Exception {
        if (message instanceof RpcResponse) {
            RpcResponse response = (RpcResponse) message;
            RpcResponseCallback callback = rpcCallbacks.remove(response.getRequestId());

            if (callback == null) {
                log.warn("Ignoring response for RPC {} from {} since it is not outstanding",
                        response.getRequestId(), NettyUtils.getRemoteAddress(channel));
            } else {
                callback.onSuccess(response);
            }
        } else if (message instanceof RpcFailure) {
            RpcFailure response = (RpcFailure) message;
            RpcResponseCallback callback = rpcCallbacks.remove(response.getRequestId());

            if (callback == null) {
                log.warn("Ignoring response for RPC {} from {} since it is not outstanding",
                        response.getRequestId(), NettyUtils.getRemoteAddress(channel));
            } else {
                callback.onFailure(new RuntimeException(response.getErrorMsg()));
            }
        } else {
            throw new IllegalStateException("Unknown response type: " + message.type());
        }
    }

    @Override
    public void channelActive() {

    }

    @Override
    public void exceptionCaught(Throwable cause) {
        if (hasOutstandingRequests()) {
            String address = NettyUtils.getRemoteAddress(channel);
            log.error("Still have {} requests outstanding when connection from {} is closed", rpcCallbacks.size(), address);
            failOutstandingRequests(cause);
        }
    }

    @Override
    public void channelInactive() {
        if (hasOutstandingRequests()) {
            String address = NettyUtils.getRemoteAddress(channel);
            log.error("Still have {} requests outstanding when connection from {} is closed", rpcCallbacks.size(), address);
            failOutstandingRequests(new IOException("Connection from " + address + " closed"));
        }
    }

    private void failOutstandingRequests(Throwable cause) {
        for (RpcResponseCallback callback : rpcCallbacks.values()) {
            try {
                callback.onFailure(cause);
            } catch (Exception e) {
                log.warn("RpcResponseCallback.onFailure throws exception", e);
            }
        }

        rpcCallbacks.clear();
    }
}