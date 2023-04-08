package jubi.common;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadUtils {

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
