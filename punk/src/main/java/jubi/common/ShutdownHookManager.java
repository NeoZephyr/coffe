package jubi.common;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ShutdownHookManager {

    private static final ShutdownHookManager MGR = new ShutdownHookManager();
    private static final ExecutorService EXECUTOR;

    private AtomicBoolean shutdownInProgress = new AtomicBoolean(false);
    private final Set<Hook> hooks = Collections.synchronizedSet(new HashSet());

    static {
        EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true)
                .setNameFormat("shutdown-hook-%01d")
                .build());

        try {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (MGR.shutdownInProgress.getAndSet(true)) {
                    log.info("Shutdown process invoked a second time: ignoring");
                } else {
                    long started = System.currentTimeMillis();
                    int timeoutCount = ShutdownHookManager.MGR.executeShutdown();
                    long ended = System.currentTimeMillis();
                    log.debug(String.format("Completed shutdown in %.3f seconds; Timeouts: %d", (double)(ended - started) / 1000.0D, timeoutCount));
                    ShutdownHookManager.shutdownExecutor();
                }
            }));
        } catch (IllegalStateException e) {
            log.warn("Failed to add the ShutdownHook", e);
        }
    }

    private static void shutdownExecutor() {
        try {
            EXECUTOR.shutdown();

            if (!EXECUTOR.awaitTermination(1, TimeUnit.SECONDS)) {
                log.error("ShutdownHookManager shutdown forcefully after {} seconds.", 1);
                EXECUTOR.shutdownNow();
            }

            log.debug("ShutdownHookManager completed shutdown.");
        } catch (InterruptedException e) {
            log.error("ShutdownHookManager interrupted while waiting for termination.", e);
            EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static ShutdownHookManager get() {
        return MGR;
    }

    public void addShutdownHook(Runnable hook, int priority) {
        if (hook == null) {
            throw new IllegalArgumentException("shutdownHook cannot be NULL");
        } else if (this.shutdownInProgress.get()) {
            throw new IllegalStateException("Shutdown in progress, cannot add a shutdownHook");
        } else {
            this.hooks.add(new Hook(hook, priority));
        }
    }

    public void addShutdownHook(Runnable hook, int priority, long timeout, TimeUnit unit) {
        if (hook == null) {
            throw new IllegalArgumentException("shutdownHook cannot be NULL");
        } else if (this.shutdownInProgress.get()) {
            throw new IllegalStateException("Shutdown in progress, cannot add a shutdownHook");
        } else {
            this.hooks.add(new ShutdownHookManager.Hook(hook, priority, timeout, unit));
        }
    }

    public boolean removeShutdownHook(Runnable hook) {
        if (this.shutdownInProgress.get()) {
            throw new IllegalStateException("Shutdown in progress, cannot remove a shutdownHook");
        } else {
            return this.hooks.remove(new ShutdownHookManager.Hook(hook, 0, 1L, TimeUnit.SECONDS));
        }
    }

    public boolean hasShutdownHook(Runnable hook) {
        return this.hooks.contains(new ShutdownHookManager.Hook(hook, 0, 1L, TimeUnit.SECONDS));
    }

    public int executeShutdown() {
        int timeouts = 0;

        for (Hook hook : getShutdownHooksInOrder()) {
            Future<?> future = EXECUTOR.submit(hook.getHook());

            try {
                future.get(hook.getTimeout(), hook.getUnit());
            } catch (TimeoutException e) {
                ++timeouts;
                future.cancel(true);
                log.warn("ShutdownHook '" + hook.getHook().getClass().getSimpleName() + "' timeout, " + e.toString(), e);
            } catch (Throwable t) {
                log.warn("ShutdownHook '" + hook.getHook().getClass().getSimpleName() + "' failed, " + t.toString(), t);
            }
        }

        return timeouts;
    }

    List<ShutdownHookManager.Hook> getShutdownHooksInOrder() {
        ArrayList list;

        synchronized(this.hooks) {
            list = new ArrayList(this.hooks);
        }

        Collections.sort(list, (Comparator<Hook>) (o1, o2) -> o2.priority - o1.priority);
        return list;
    }

    @Data
    static class Hook {
        private final Runnable hook;
        private final int priority;
        private final long timeout;
        private final TimeUnit unit;

        Hook(Runnable hook, int priority) {
            this(hook, priority, 1, TimeUnit.SECONDS);
        }

        Hook(Runnable hook, int priority, long timeout, TimeUnit unit) {
            this.hook = hook;
            this.priority = priority;
            this.timeout = timeout;
            this.unit = unit;
        }

        public int hashCode() {
            return this.hook.hashCode();
        }

        public boolean equals(Object obj) {
            boolean eq = false;
            if (obj != null && obj instanceof Hook) {
                eq = this.hook == ((Hook)obj).hook;
            }

            return eq;
        }
    }
}
