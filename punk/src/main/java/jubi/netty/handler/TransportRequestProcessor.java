package jubi.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import jubi.netty.client.TransportClient;
import jubi.netty.protocol.Message;
import jubi.netty.protocol.RequestMessage;
import jubi.netty.protocol.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

@Slf4j
public class TransportRequestProcessor extends MessageProcessor<RequestMessage> {

    private Channel channel;
    private TransportClient client;

    public TransportRequestProcessor(Channel channel, TransportClient client) {
        this.channel = channel;
        this.client = client;
    }

    @Override
    public void handle(RequestMessage message) throws Exception {
        if (message instanceof RpcRequest) {
            //
        } else {
            throw new IllegalArgumentException("Unknown request type: " + message.type());
        }
    }

    // rpcHandler 代理处理
    @Override
    public void channelActive() {

    }

    @Override
    public void exceptionCaught(Throwable cause) {

    }

    @Override
    public void channelInactive() {

    }

    private ChannelFuture respond(Message message) {
        SocketAddress address = channel.remoteAddress();
        return channel.writeAndFlush(message).addListener(future -> {
            if (future.isSuccess()) {
                log.trace("Sent result {} to client {}", message, address);
            } else {
                log.error(String.format("Error sending result %s to %s; closing connection", message, address), future.cause());
                channel.close();
            }
        });
    }
}

//try {
//    rpcHandler.receive(reverseClient, req.body().nioByteBuffer(), new RpcResponseCallback() {
//        @Override
//        public void onSuccess(ByteBuffer response) {
//            respond(new RpcResponse(req.requestId, new NioManagedBuffer(response)));
//        }
//
//        @Override
//        public void onFailure(Throwable e) {
//            respond(new RpcFailure(req.requestId, Throwables.getStackTraceAsString(e)));
//        }
//    });
//} catch (Exception e) {
//    logger.error("Error while invoking RpcHandler#receive() on RPC id " + req.requestId, e);
//    respond(new RpcFailure(req.requestId, Throwables.getStackTraceAsString(e)));
//} finally {
//    req.body().release();
//}

// * The handler keeps track of all client instances that communicate with it, so that the RpcEnv
// * knows which `TransportClient` instance to use when sending RPCs to a client endpoint (i.e.,
// * one that is not listening for incoming connections, but rather needs to be contacted via the
// * client socket).
// *
// * Events are sent on a per-connection basis, so if a client opens multiple connections to the
// * RpcEnv, multiple connection / disconnection events will be created for that client (albeit
// * with different `RpcAddress` information).
// */
//        private[netty] class NettyRpcHandler(
//        dispatcher: Dispatcher,
//        nettyEnv: NettyRpcEnv,
//        streamManager: StreamManager) extends RpcHandler with Logging {
//
//        // A variable to track the remote RpcEnv addresses of all clients
//        private val remoteAddresses = new ConcurrentHashMap[RpcAddress, RpcAddress]()
//
//        override def receive(
//        client: TransportClient,
//        message: ByteBuffer,
//        callback: RpcResponseCallback): Unit = {
//        val messageToDispatch = internalReceive(client, message)
//        dispatcher.postRemoteMessage(messageToDispatch, callback)
//        }
//
//        override def receive(
//        client: TransportClient,
//        message: ByteBuffer): Unit = {
//        val messageToDispatch = internalReceive(client, message)
//        dispatcher.postOneWayMessage(messageToDispatch)
//        }
//
//        private def internalReceive(client: TransportClient, message: ByteBuffer): RequestMessage = {
//        val addr = client.getChannel().remoteAddress().asInstanceOf[InetSocketAddress]
//        assert(addr != null)
//        val clientAddr = RpcAddress(addr.getHostString, addr.getPort)
//        val requestMessage = RequestMessage(nettyEnv, client, message)
//        if (requestMessage.senderAddress == null) {
//        // Create a new message with the socket address of the client as the sender.
//        new RequestMessage(clientAddr, requestMessage.receiver, requestMessage.content)
//        } else {
//        // The remote RpcEnv listens to some port, we should also fire a RemoteProcessConnected for
//        // the listening address
//        val remoteEnvAddress = requestMessage.senderAddress
//        if (remoteAddresses.putIfAbsent(clientAddr, remoteEnvAddress) == null) {
//        dispatcher.postToAll(RemoteProcessConnected(remoteEnvAddress))
//        }
//        requestMessage
//        }
//        }
//
//        override def getStreamManager: StreamManager = streamManager
//
//        override def exceptionCaught(cause: Throwable, client: TransportClient): Unit = {
//        val addr = client.getChannel.remoteAddress().asInstanceOf[InetSocketAddress]
//        if (addr != null) {
//        val clientAddr = RpcAddress(addr.getHostString, addr.getPort)
//        dispatcher.postToAll(RemoteProcessConnectionError(cause, clientAddr))
//        // If the remove RpcEnv listens to some address, we should also fire a
//        // RemoteProcessConnectionError for the remote RpcEnv listening address
//        val remoteEnvAddress = remoteAddresses.get(clientAddr)
//        if (remoteEnvAddress != null) {
//        dispatcher.postToAll(RemoteProcessConnectionError(cause, remoteEnvAddress))
//        }
//        } else {
//        // If the channel is closed before connecting, its remoteAddress will be null.
//        // See java.net.Socket.getRemoteSocketAddress
//        // Because we cannot get a RpcAddress, just log it
//        logError("Exception before connecting to the client", cause)
//        }
//        }
//
//        override def channelActive(client: TransportClient): Unit = {
//        val addr = client.getChannel().remoteAddress().asInstanceOf[InetSocketAddress]
//        assert(addr != null)
//        val clientAddr = RpcAddress(addr.getHostString, addr.getPort)
//        dispatcher.postToAll(RemoteProcessConnected(clientAddr))
//        }
//
//        override def channelInactive(client: TransportClient): Unit = {
//        val addr = client.getChannel.remoteAddress().asInstanceOf[InetSocketAddress]
//        if (addr != null) {
//        val clientAddr = RpcAddress(addr.getHostString, addr.getPort)
//        nettyEnv.removeOutbox(clientAddr)
//        dispatcher.postToAll(RemoteProcessDisconnected(clientAddr))
//        val remoteEnvAddress = remoteAddresses.remove(clientAddr)
//        // If the remove RpcEnv listens to some address, we should also  fire a
//        // RemoteProcessDisconnected for the remote RpcEnv listening address
//        if (remoteEnvAddress != null) {
//        dispatcher.postToAll(RemoteProcessDisconnected(remoteEnvAddress))
//        }
//        } else {
//        // If the channel is closed before connecting, its remoteAddress will be null. In this case,
//        // we can ignore it since we don't fire "Associated".
//        // See java.net.Socket.getRemoteSocketAddress
//        }
//        }
//        }