package jubi.netty.core;

import jubi.JubiException;
import queue.hole.Endpoint;
import jubi.netty.server.RpcService;
import jubi.netty.client.RpcResponseCallback;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class Dispatcher {

    private volatile boolean stopped = false;
    private CountDownLatch shutdownLatch = new CountDownLatch(1);
    private ConcurrentHashMap<String, MessageLoop> endpoints = new ConcurrentHashMap<>();
    private MessageLoop sharedLoop = new SharedMessageLoop(this);

    public Dispatcher() {
    }

    public void registerEndpoint(Endpoint endpoint, RpcService service) {
        synchronized (this) {
            if (stopped) {
                throw new IllegalStateException("RpcEnv has been stopped");
            }
            if (endpoints.containsKey(endpoint.name)) {
                throw new IllegalArgumentException(String.format("There is already an RpcEndpoint called %s", endpoint.name));
            }
            sharedLoop.register(endpoint.name, service);
            endpoints.put(endpoint.name, sharedLoop);
        }
    }

    public void unregisterEndpoint(String name) {
        MessageLoop loop = endpoints.remove(name);

        if (loop != null) {
            loop.unregister(name);
        }
    }

    public void postMessage(Endpoint endpoint, InboxMessage message, RpcResponseCallback callback) {
        Throwable error = null;

        synchronized (this) {
            MessageLoop loop = endpoints.get(endpoint.name);

            if (stopped) {
                error = new JubiException("Dispatcher already stopped");
            } else if (loop == null) {
                error = new JubiException(String.format("Could not find %s.", endpoint.name));
            } else {
                loop.post(endpoint.name, message);
            }
        }

        if (error != null) {
            callback.onFailure(error);
        }
    }

    public void stop() throws InterruptedException {
        if (stopped) {
            return;
        }

        synchronized (this) {
            if (stopped) {
                return;
            }

            stopped = true;
        }

        for (Map.Entry<String, MessageLoop> entry : endpoints.entrySet()) {
            unregisterEndpoint(entry.getKey());
        }

        sharedLoop.stop();
        shutdownLatch.countDown();
    }

    void awaitTermination() throws InterruptedException {
        shutdownLatch.await();
    }
}
