package queue.hole;

import jubi.netty.server.RpcService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 1. DedicatedMessageLoop
 * 2. SharedMessageLoop
 */
@Slf4j
public abstract class MessageLoop {
    private LinkedBlockingQueue<Inbox> inboxes = new LinkedBlockingQueue<>();
    private boolean stopped = false;
    protected ExecutorService pool;
    protected Dispatcher dispatcher;
    protected Runnable loopRunner = new Runnable() {
        @Override
        public void run() {
            try {
                while (true) {
                    try {
                        Inbox inbox = inboxes.take();

                        if (inbox == MessageLoop.PoisonPill) {
                            // Put PoisonPill back so that other threads can see it.
                            activate(MessageLoop.PoisonPill);
                            return;
                        }

                        inbox.process();
                    } catch (Exception e) {
                        log.warn("inbox process error.", e);
                    }
                }
            } catch (Throwable t) {
                pool.execute(loopRunner);
            }
        }
    };

    protected static Inbox PoisonPill = new Inbox(null, null);

    public abstract void post(String name, InboxMessage message);

    public abstract void register(String name, RpcService service);

    public abstract void unregister(String name);

    public void activate(Inbox inbox) {
        inboxes.offer(inbox);
    }

    public void stop() throws InterruptedException {
        synchronized (this) {
            if (!stopped) {
                activate(PoisonPill);
                pool.shutdown();
                stopped = true;
            }
        }

        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }
}

