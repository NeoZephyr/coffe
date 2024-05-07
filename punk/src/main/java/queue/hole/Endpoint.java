package queue.hole;

import common.ThreadUtils;
import jubi.netty.protocol.Message;
import jubi.netty.util.Utils;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

public class Endpoint {

    public Address address;
    public String name;

    private ConcurrentHashMap<Address, Outbox> outboxes = new ConcurrentHashMap<>();

    private AtomicBoolean stopped = new AtomicBoolean(false);

    private Dispatcher dispatcher;

    public ThreadPoolExecutor connectExecutor;

    public Endpoint(Config config, String name) throws Exception {
        this.address = new Address(config.host, config.port);
        this.name = name;
        connectExecutor = ThreadUtils.newDaemonCachedThreadPool(1, "netty-rpc-connect");
        dispatcher = new Dispatcher();

        if (config.serverMode) {
            try {
                Utils.startService(config.port, port -> startServer(config.host, port));
            } catch (Exception e) {
                shutdown();
                throw e;
            }
        }
    }

    private void startServer(String host, int port) {}

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

        // close server
    }

    public void send(Endpoint receiver, Message message) throws IOException {
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
            outbox.send(new OutboxMessage(null));
        }
    }

    // receive -> dispatcher -> loop(find by endpoint name) -> post and activate inbox

    public void removeOutbox(Address address) {
        Outbox outbox = outboxes.remove(address);

        if (outbox != null) {
            outbox.stop();
        }
    }

}
