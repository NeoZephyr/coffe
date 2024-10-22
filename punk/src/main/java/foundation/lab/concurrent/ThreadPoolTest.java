package foundation.lab.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class ThreadPoolTest {
    public static void main(String[] args) throws InterruptedException {
        // createThreadPool();
        shutdown();
    }

    public static void shutdown() throws InterruptedException {
        ThreadPoolTaskExecutor worker = new ThreadPoolTaskExecutor();
        worker.setMaxPoolSize(3);
        worker.setCorePoolSize(3);
        worker.setQueueCapacity(1000);
        worker.setKeepAliveSeconds(60);
        worker.setThreadNamePrefix("identity-sink-worker-");
        worker.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        worker.setWaitForTasksToCompleteOnShutdown(false);
        worker.setAwaitTerminationSeconds(2);
        worker.initialize();

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            worker.submit(() -> {
                log.info("before exec task {}", finalI);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.warn("exec task interrupted {}", finalI, e);
                    // throw new RuntimeException(e);
                }

                log.info("after exec task {}", finalI);
            });
        }

        Thread.sleep(3000);


        // 1. stop all actively executing tasks
        // 2. halts the processing of waiting tasks, and returns a list of the tasks that were awaiting execution
        // 3. stop processing actively executing tasks via Thread.interrupt, any task that fails to respond to interrupts may never terminate
        // List<Runnable> runnables = worker.getThreadPoolExecutor().shutdownNow();
        // log.info("waiting tasks size: {}", runnables.size());

        // 1. previously submitted tasks are executed
        // 2. no new tasks will be accepted
        // worker.getThreadPoolExecutor().shutdown();

        worker.shutdown();
        worker.submit(() -> System.out.println("hello"));
        Thread.sleep(10 * 1000);
        log.info("======= finish");
        worker.submit(() -> System.out.println("hello"));
    }

    private static void createThreadPool() throws InterruptedException {
        // LinkedBlockingQueue
        // Executors.newSingleThreadExecutor();
        // Executors.newFixedThreadPool(1);

        // SynchronousQueue
        // Executors.newCachedThreadPool();

        // DelayedWorkQueue
        // Executors.newScheduledThreadPool(1);

        // Executors.newWorkStealingPool(1);

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // 是否已经 shutdown
        System.out.println(executorService.isShutdown());

        // 已经 shutdown 并且任务全部完成
        System.out.println(executorService.isTerminated());

        // 等待 terminate
        executorService.awaitTermination(100, TimeUnit.MILLISECONDS);

        executorService.shutdown();
        executorService.shutdownNow();

        new ThreadPoolExecutor.AbortPolicy();
        new ThreadPoolExecutor.DiscardPolicy();
        new ThreadPoolExecutor.DiscardOldestPolicy();
        new ThreadPoolExecutor.CallerRunsPolicy();

        PauseThreadPool threadPool =
                new PauseThreadPool(5, 20, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        Runnable run = () -> {
            System.out.printf("thread: %s running\n", Thread.currentThread().getName());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        for (int i = 0; i < 1000; ++i) {
            threadPool.execute(run);
        }

        Thread.sleep(1000);
        System.out.println("pause");
        threadPool.pause();
        Thread.sleep(5000);
        System.out.println("resume");
        threadPool.resume();
    }

    static class PauseThreadPool extends ThreadPoolExecutor {
        private boolean pause;
        private ReentrantLock pauseLock = new ReentrantLock();
        private Condition pauseCond = pauseLock.newCondition();

        public PauseThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        public PauseThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        }

        public PauseThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        }

        public PauseThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        }

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
            pauseLock.lock();

            try {
                while (pause) {
                    pauseCond.await();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                pauseLock.unlock();
            }
        }

        public void pause() {
            try {
                pauseLock.lock();
                pause = true;
            } finally {
                pauseLock.unlock();
            }
        }

        public void resume() {
            try {
                pauseLock.lock();
                pause = false;
                pauseCond.signalAll();
            } finally {
                pauseLock.unlock();
            }
        }
    }

    class A {
        int a;
    }

    class B extends A {

        public void test() {
            int a = 10;
            System.out.println(a);

            Runnable runnable = () -> {
                // int a = 100;
                System.out.println(a);
            };

            // a = 100;

            runnable.run();

            ((Runnable) () -> {

                int b = 100;

                class C extends B {
                    public void test() {
                        System.out.println(a);
                    }
                }

                System.out.println(a);
                System.out.println(new C().a);
            }).run();
        }
    }
}
