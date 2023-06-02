package jubi.netty.core;

import jubi.netty.client.RpcResponseCallback;
import jubi.netty.client.TransportClient;
import jubi.netty.protocol.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

@Slf4j
public class OutboxMessage implements RpcResponseCallback {

    TransportClient client = null;
    Long requestId = null;
    ByteBuffer content = null;
    Consumer<Throwable> _onFailure = null;

    public OutboxMessage(ByteBuffer content) {
        this.content = content;
    }

    void sendWith(TransportClient client) {
        this.client = client;
        this.requestId = client.sendRpc(null, this);
    }

    @Override
    public void onSuccess(RpcResponse response) {

    }

    @Override
    public void onFailure(Throwable e) {
        if (_onFailure != null) {
            _onFailure.accept(e);
        } else {
            log.warn("send message failed.", e);
        }
    }

    //
//    private[netty] case class RpcOutboxMessage(
//            content: ByteBuffer,
//            _onFailure: (Throwable) => Unit,
//    _onSuccess: (TransportClient, ByteBuffer) => Unit)
//            extends OutboxMessage with RpcResponseCallback with Logging {

//        private[netty] def removeRpcRequest(): Unit = {
//        if (client != null) {
//            client.removeRpcRequest(requestId)
//        } else {
//            logError("Ask terminated before connecting successfully")
//        }
//  }
//
//        def onTimeout(): Unit = {
//                removeRpcRequest()
//        }
//
//        def onAbort(): Unit = {
//                removeRpcRequest()
//        }

//        override def onSuccess(response: ByteBuffer): Unit = {
//                _onSuccess(client, response)
//        }
//
//    }
}
