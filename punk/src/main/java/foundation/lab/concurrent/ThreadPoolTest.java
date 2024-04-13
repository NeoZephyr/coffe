package foundation.lab.concurrent;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPoolTest {
    public static void main(String[] args) throws InterruptedException {
        createThreadPool();
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
