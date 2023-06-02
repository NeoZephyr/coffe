package jubi.netty.core;

import jubi.JubiException;
import jubi.netty.RpcEndpoint;
import jubi.netty.server.RpcService;
import jubi.netty.client.RpcResponseCallback;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class Dispatcher {

    private volatile boolean stopped = false;
    private CountDownLatch shutdownLatch = new CountDownLatch(1);
    private ConcurrentHashMap<String, MessageLoop> endpoints = new ConcurrentHashMap<>();
    private RpcEnv env;
    private MessageLoop sharedLoop = new SharedMessageLoop(env.conf, this);

    public Dispatcher(RpcEnv env) {
        this.env = env;
    }

    public void registerEndpoint(RpcEndpoint endpoint, RpcService service) {
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

    public void postMessage(RpcEndpoint endpoint, InboxMessage message, RpcResponseCallback callback) {
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

    void stop() throws InterruptedException {
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
