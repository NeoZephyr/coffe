package foundation.lab.concurrent;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class CountDownLatchTest {
    public static void main(String[] args) throws InterruptedException {
        // redpack();
        race();
    }

    private static void redpack() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(5);
        Runnable run = () -> {
            System.out.printf("Thread %s start\n", Thread.currentThread().getName());
            latch.countDown();
        };

        for (int i = 0; i < 5; ++i) {
            new Thread(run).start();
        }

        latch.await();

        System.out.println("All thread complete");
    }

    private static void race() throws InterruptedException {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(5);

        Runnable run = () -> {
            System.out.printf("Thread %s Ready...\n", Thread.currentThread().getName());
            try {
                startLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Thread %s Running...\n", Thread.currentThread().getName());
            try {
                Thread.sleep(new Random().nextInt(10) * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.printf("Thread %s Completing...\n", Thread.currentThread().getName());
                endLatch.countDown();
            }
        };

        for (int i = 0; i < 5; ++i) {
            new Thread(run).start();
        }

        Thread.sleep(100);

        System.out.println("=== start!");
        startLatch.countDown();

        endLatch.await();
        System.out.println("=== end!");
    }
}
