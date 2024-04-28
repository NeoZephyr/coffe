package network.rpc.core.transport;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

public class InFlightRequests implements Closeable {

    private final static long TIMEOUT = 10 * 1000L;
    private final Semaphore semaphore = new Semaphore(10);
    private final Map<Integer, ResponseFuture> futures = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledFuture scheduledFuture;

    public InFlightRequests() {
        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(this::removeTimeoutFutures, TIMEOUT, TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void put(ResponseFuture future) throws TimeoutException, InterruptedException {
        if (semaphore.tryAcquire(TIMEOUT, TimeUnit.MILLISECONDS)) {
            futures.put(future.getRequestId(), future);
        } else {
            throw new TimeoutException();
        }
    }

    public ResponseFuture remove(int requestId) {
        ResponseFuture future = futures.remove(requestId);

        if (future != null) {
            semaphore.release();
        }

        return future;
    }

    // TODO thread safe
    private void removeTimeoutFutures() {
        futures.entrySet().removeIf(entry -> {
            if (System.nanoTime() - entry.getValue().getTimestamp() > TIMEOUT * 1000000L) {
                semaphore.release();
                return true;
            } else {
                return false;
            }
        });
    }

    @Override
    public void close() {
        scheduledFuture.cancel(true);
        scheduledExecutorService.shutdown();
    }
}