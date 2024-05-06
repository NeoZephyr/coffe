package common;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class ThreadUtils {

    public static ScheduledExecutorService newDaemonSingleThreadScheduledExecutor(String name, boolean execAfterShutdown) {
        ThreadFactory threadFactory = new DaemonThreadFactory(name);
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, threadFactory);
        executor.setRemoveOnCancelPolicy(true);
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(execAfterShutdown);
        return executor;
    }

    public static ThreadPoolExecutor newDaemonQueuedThreadPool(int poolSize, int queueSize, long keepAliveMillis, String name) {
        ThreadFactory factory = new DaemonThreadFactory(name);
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(queueSize);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, poolSize, keepAliveMillis, TimeUnit.MILLISECONDS, queue, factory);
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    public static ThreadPoolExecutor newDaemonCachedThreadPool(int poolSize, String prefix) {
        return newDaemonCachedThreadPool(poolSize, 60, prefix);
    }

    public static ExecutorService newDaemonFixedThreadPool(int poolSize, String prefix) {
        return Executors.newFixedThreadPool(poolSize, getThreadFactory(prefix));
    }

    public static ThreadPoolExecutor newDaemonCachedThreadPool(int poolSize, long keepAliveSeconds, String prefix) {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(poolSize, poolSize, keepAliveSeconds, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), getThreadFactory(prefix));
        poolExecutor.allowCoreThreadTimeOut(true);
        return poolExecutor;
    }

    public static ThreadFactory getThreadFactory(String name) {
        return new ThreadFactoryBuilder().setDaemon(true)
                .setNameFormat(name + "-%d")
                .build();
    }

    public static void shutdownThreadPool(ExecutorService threadPool, int waitSeconds) throws InterruptedException {
        if (threadPool == null) {
            return;
        }
        threadPool.shutdown();

        if (!threadPool.awaitTermination(waitSeconds, TimeUnit.SECONDS)) {
            threadPool.shutdownNow();

            if (!threadPool.awaitTermination(waitSeconds, TimeUnit.SECONDS)) {
                log.warn("Thread pool don't stop gracefully.");
            }
        }
    }
}
