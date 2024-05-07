package queue.hole;

import common.ThreadUtils;
import jubi.netty.server.RpcService;

import java.util.concurrent.ConcurrentHashMap;

public class SharedMessageLoop extends MessageLoop {

    private ConcurrentHashMap<String, Inbox> inboxes = new ConcurrentHashMap();

    public SharedMessageLoop(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        int threadNum = Runtime.getRuntime().availableProcessors();
        pool = ThreadUtils.newDaemonCachedThreadPool(threadNum, "dispatcher-event-loop");

        for (int i = 0; i < threadNum; i++) {
            pool.execute(loopRunner);
        }
    }

    @Override
    public void post(String name, InboxMessage message) {
        Inbox inbox = inboxes.get(name);
        inbox.post(message);
        activate(inbox);
    }

    @Override
    public void register(String name, RpcService service) {
        Inbox inbox = new Inbox(name, service);
        inboxes.put(name, inbox);

        // activate to handle the OnStart message.
        activate(inbox);
    }

    @Override
    public void unregister(String name) {
        Inbox inbox = inboxes.remove(name);

        if (inbox != null) {
            inbox.stop();

            // activate to handle the OnStop message
            activate(inbox);
        }
    }
}