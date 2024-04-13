package foundation.lab.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.*;
import java.util.stream.IntStream;

public class AtomicTest {
    public static void main(String[] args) {
        // atomicArray();
        // fieldUpdater();
        // atomicLong();
        // longAdder();
        accumulator();
    }

    private static void atomicArray() {
        AtomicIntegerArray array = new AtomicIntegerArray(1000);
        Runnable incrRun = () -> {
            for (int i = 0; i < array.length(); ++i) {
                array.getAndAdd(i, 10);
            }
        };
        Runnable decrRun = () -> {
            for (int i = 0; i < array.length(); ++i) {
                array.getAndAdd(i, -5);
            }
        };
        Thread[] incrThreads = new Thread[100];
        Thread[] decrThreads = new Thread[100];

        for (int i = 0; i < 100; ++i) {
            incrThreads[i] = new Thread(incrRun);
            decrThreads[i] = new Thread(decrRun);
        }

        for (int i = 0; i < 100; ++i) {
            incrThreads[i].start();
            decrThreads[i].start();
        }

        for (int i = 0; i < 100; ++i) {
            try {
                incrThreads[i].join();
                decrThreads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < array.length(); ++i) {
            System.out.println(array.get(i));
        }
    }

    private static void fieldUpdater() {
        Present present1 = new Present();
        Present present2 = new Present();

        // 1. 必须可见
        // 2. 不能是 static 变量
        AtomicIntegerFieldUpdater<Present> updater =
                AtomicIntegerFieldUpdater.newUpdater(Present.class, "score");
        Runnable run = () -> {
            for (int i = 0; i < 10000; ++i) {
                ++present1.score;
                updater.getAndIncrement(present2);
            }
        };

        Thread thread1 = new Thread(run);
        Thread thread2 = new Thread(run);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("present1 score: " + present1.score);
        System.out.println("present2 score: " + present2.score);
    }

    private static void atomicLong() {
        // 每次更新都要 flush 到 shared cache, 然后 refresh 到 local cache
        AtomicLong counter = new AtomicLong();
        ExecutorService threadPool = Executors.newFixedThreadPool(20);
        Runnable run = () -> {
            for (int i = 0; i < 10000; ++i) {
                counter.getAndIncrement();
            }
        };
        long startMillis = System.currentTimeMillis();

        for (int i = 0; i < 10000; ++i) {
            threadPool.submit(run);
        }

        threadPool.shutdown();
        try {
            threadPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endMillis = System.currentTimeMillis();
        System.out.printf("cost millis: %d, count: %d\n", (endMillis - startMillis), counter.get());
    }

    private static void longAdder() {
        // 每个线程会自己有一个计数器，在加的过程中线程直接不需要同步，不需要 flush 和 refresh
        // 竞争激烈的情况下 LongAdder 优于 AtomicLong
        LongAdder counter = new LongAdder();
        ExecutorService threadPool = Executors.newFixedThreadPool(20);
        Runnable run = () -> {
            for (int i = 0; i < 10000; ++i) {
                counter.increment();
            }
        };
        long startMillis = System.currentTimeMillis();

        for (int i = 0; i < 10000; ++i) {
            threadPool.submit(run);
        }

        threadPool.shutdown();
        try {
            threadPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endMillis = System.currentTimeMillis();
        System.out.printf("cost millis: %d, count: %d\n", (endMillis - startMillis), counter.sum());
    }

    private static void accumulator() {
        LongAccumulator accumulator = new LongAccumulator((x, y) -> 1 + (x + y), 0);
        ExecutorService threadPool = Executors.newFixedThreadPool(20);
        IntStream.range(1, 10).forEach(i -> threadPool.submit(() -> accumulator.accumulate(i)));
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(accumulator.getThenReset());
    }

    static class Present {
        volatile int score;
    }
}
