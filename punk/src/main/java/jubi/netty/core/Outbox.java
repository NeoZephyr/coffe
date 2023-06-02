package jubi.netty.core;

import jubi.JubiException;
import jubi.netty.RpcAddress;
import jubi.netty.client.TransportClient;

import java.util.LinkedList;
import java.util.concurrent.Future;

public class Outbox {

    RpcAddress address;
    private RpcEnv rpcEnv;
    private LinkedList<OutboxMessage> messages = new LinkedList<>();
    private boolean stopped = false;
    private boolean draining = false;
    private Future<Void> connectFuture = null;
    private volatile TransportClient client = null;

    public Outbox(RpcAddress address, RpcEnv rpcEnv) {
        this.address = address;
        this.rpcEnv = rpcEnv;
    }

    public void send(OutboxMessage message) {
        boolean dropped = false;

        synchronized (this) {
            if (stopped) {
                dropped = true;
            } else {
                messages.add(message);
            }
        }

        if (dropped) {
            message.onFailure(new JubiException("Message is dropped because Outbox is stopped"));
        } else {
            drainOutbox();
        }
    }

    public void stop() {
        synchronized (this) {
            if (stopped) {
                return;
            }

            stopped = true;

            if (connectFuture != null) {
                connectFuture.cancel(true);
            }

            closeClient();
        }

        do {
            OutboxMessage message = messages.poll();

            if (message == null) {
                break;
            }
            message.onFailure(new JubiException("Message is dropped because Outbox is stopped"));
        } while (true);
    }

    private void drainOutbox() {
        OutboxMessage message = null;

        synchronized (this) {
            if (stopped) {
                return;
            }

            if (connectFuture != null) {
                // We are connecting to the remote address, just exit
                return;
            }

            if (client == null) {
                launchConnectTask();
                return;
            }

            if (draining) {
                return;
            }

            message = messages.poll();

            if (message == null) {
                return;
            }
            draining = true;
        }

        while (true) {
            try {
                if (client != null) {
                    message.sendWith(client);
                }
            } catch (Exception e) {
                handleNetworkFailure(e);
                return;
            }

            synchronized (this) {
                if (stopped) {
                    return;
                }

                message = messages.poll();

                if (message == null) {
                    draining = false;
                    return;
                }
            }
        }
    }

    private void launchConnectTask() {
        Outbox that = this;
        rpcEnv.connectExecutor.submit(() -> {
            try {
                TransportClient client = rpcEnv.createClient(address);

                synchronized (that) {
                    that.client = client;

                    if (stopped) {
                        closeClient();
                    }
                }
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                synchronized (that) {
                    connectFuture = null;
                }
                handleNetworkFailure(e);
                return;
            }

            synchronized (that) {
                connectFuture = null;
            }
            drainOutbox();
        });
    }

    private void handleNetworkFailure(Exception e) {
        synchronized (this) {
            if (stopped) {
                return;
            }
            stopped = true;
            closeClient();
        }

        rpcEnv.removeOutbox(address);

        do {
            OutboxMessage message = messages.poll();

            if (message == null) {
                break;
            }

            message.onFailure(e);
        } while (true);
    }

    private synchronized void closeClient() {
        // Just set client to null. Don't close it in order to reuse the connection.
        client = null;
    }
}
