package jubi.netty.core;

import jubi.netty.server.RpcService;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

@Slf4j
public class Inbox {
    private String name;
    private RpcService service;

    private LinkedList<InboxMessage> messages = new LinkedList<>();
    private boolean stopped = false;
    private boolean enableConcurrent = false;
    private int activeThreadNum = 0;

    public static InboxMessage OnStart = new InboxMessage();
    public static InboxMessage OnStop = new InboxMessage();

    public Inbox(String name, RpcService service) {
        this.name = name;
        this.service = service;
        messages.add(OnStart);
    }

    public synchronized void post(InboxMessage message) {
        if (stopped) {
            log.warn(String.format("Drop message bacause endpoint %s is stopped", name));
        } else {
            messages.add(message);
        }
    }

    public void process() {
        InboxMessage message = null;

        synchronized (this) {
            if (!enableConcurrent && activeThreadNum != 0) {
                return;
            }

            message = messages.poll();

            if (message != null) {
                activeThreadNum += 1;
            } else {
                return;
            }
        }

        while (true) {
            try {
                if (message == OnStart) {
                    service.onStart();

                    synchronized (this) {
                        enableConcurrent = true;
                    }
                } else if (message == OnStop) {
                    // OnStop should be the last message
                    service.onStop();
                } else {
                    service.receiveAndReply(message.content);
                }
            } catch (Exception e) {}

            synchronized (this) {
                if (!enableConcurrent && activeThreadNum != 1) {
                    activeThreadNum -= 1;
                    return;
                }

                message = messages.poll();

                if (message == null) {
                    activeThreadNum -= 1;
                    return;
                }
            }
        }
    }

    public synchronized void stop() {
        if (!stopped) {
            enableConcurrent = false;
            stopped = true;
            messages.add(OnStop);
        }
    }
}