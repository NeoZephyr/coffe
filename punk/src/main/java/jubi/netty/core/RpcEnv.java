package jubi.netty.core;

import jubi.common.ThreadUtils;
import jubi.config.JubiConf;
import jubi.netty.RpcAddress;
import jubi.netty.RpcEndpoint;
import jubi.netty.TransportConf;
import jubi.netty.TransportContext;
import jubi.netty.client.RpcResponseCallback;
import jubi.netty.client.TransportClient;
import jubi.netty.client.TransportClientFactory;
import jubi.netty.protocol.Message;
import jubi.netty.server.RpcCallContext;
import jubi.netty.server.TransportServer;
import jubi.netty.util.Utils;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

public class RpcEnv {
    private String host;
    private TransportConf transportConf;
    JubiConf conf;
    private Dispatcher dispatcher;
    private TransportContext transportContext;
    private TransportClientFactory clientFactory = transportContext.createClientFactory();

    private volatile TransportServer server;
    private AtomicBoolean stopped = new AtomicBoolean(false);
    private ConcurrentHashMap<RpcAddress, Outbox> outboxes = new ConcurrentHashMap<>();

    ThreadPoolExecutor connectExecutor;

    public static RpcEnv create(RpcEnvConfig config) throws Exception {
        RpcEnv rpcEnv = new RpcEnv();
        rpcEnv.conf = config.conf;
        rpcEnv.transportConf = new TransportConf(config.conf);
        rpcEnv.host = config.host;
        rpcEnv.transportContext = new TransportContext(rpcEnv.transportConf);
        rpcEnv.connectExecutor = ThreadUtils.newDaemonCachedThreadPool(1, "netty-rpc-connection");
        rpcEnv.dispatcher = new Dispatcher(rpcEnv);

        if (config.serverMode) {
            try {
                Utils.startService(config.port, port -> rpcEnv.startServer(config.host, port));
            } catch (Exception e) {
                rpcEnv.shutdown();
                throw e;
            }
        }

        return rpcEnv;
    }

    public void send(RpcEndpoint receiver, Message message) throws IOException {
        Outbox outbox = outboxes.get(receiver.address);

        if (outbox == null) {
            outbox = new Outbox(receiver.address, this);
            Outbox existOutbox = outboxes.putIfAbsent(receiver.address, outbox);

            if (existOutbox != null) {
                outbox = existOutbox;
            }
        }

        if (stopped.get()) {
            outboxes.remove(receiver.address);
            outbox.stop();
        } else {
            outbox.send(new OutboxMessage(serialize(receiver, message)));
        }
    }

    public void receive(TransportClient client, ByteBuffer content, RpcResponseCallback callback) throws IOException, ClassNotFoundException {
        InetSocketAddress address = (InetSocketAddress) client.getChannel().remoteAddress();
        RpcAddress clientAddress = new RpcAddress(address.getHostString(), address.getPort());
        ByteArrayInputStream bais = new ByteArrayInputStream(content.array());
        ObjectInputStream ois = new ObjectInputStream(bais);
        RpcAddress sender = readAddress(ois);
        RpcAddress receiver = readAddress(ois);
        RpcEndpoint endpoint = new RpcEndpoint(receiver, ois.readUTF());
        Message message = (Message) ois.readObject();

        // val rpcCallContext =
        //      new RemoteNettyRpcCallContext(nettyEnv, callback, message.senderAddress)
        InboxMessage inboxMessage = new InboxMessage(sender, message, new RpcCallContext(this, callback, sender));
        dispatcher.postMessage(endpoint, inboxMessage, callback);
    }

    public void shutdown() throws IOException, InterruptedException {
        if (!stopped.compareAndSet(false, true)) {
            return;
        }

        for (Outbox outbox : outboxes.values()) {
            outboxes.remove(outbox.address);
            outbox.stop();
        }

        if (dispatcher != null) {
            dispatcher.stop();
        }

        if (server != null) {
            server.close();
        }

        if (clientFactory != null) {
            clientFactory.close();
        }
    }

    public void awaitTermination() throws InterruptedException {
        dispatcher.awaitTermination();
    }

    void removeOutbox(RpcAddress address) {
        Outbox outbox = outboxes.remove(address);

        if (outbox != null) {
            outbox.stop();
        }
    }

    TransportClient createClient(RpcAddress address) throws IOException, InterruptedException {
        return clientFactory.createClient(address.host, address.port);
    }

    private void startServer(String host, int port) {
        server = transportContext.createServer(host, port);
    }

    private RpcAddress address() {
        if (server == null) {
            return new RpcAddress(host, server.getPort());
        } else {
            return null;
        }
    }

    private ByteBuffer serialize(RpcEndpoint receiver, Message message) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        writeAddress(oos, address());
        writeAddress(oos, receiver.address);
        oos.writeUTF(receiver.name);
        oos.writeObject(message);
        oos.writeObject(message);
        oos.close();
        return ByteBuffer.wrap(baos.toByteArray());
    }

    private void writeAddress(ObjectOutputStream oos, RpcAddress address) throws IOException {
        if (address == null) {
            oos.writeBoolean(false);
        } else {
            oos.writeBoolean(true);
            oos.writeUTF(address.host);
            oos.writeInt(address.port);
        }
    }

    private RpcAddress readAddress(ObjectInputStream ois) throws IOException {
        boolean hasAddress = ois.readBoolean();

        if (hasAddress) {
            return new RpcAddress(ois.readUTF(), ois.readInt());
        } else {
            return null;
        }
    }
}