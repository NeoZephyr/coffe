package foundation.lab.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LockTest {
    public static void main(String[] args) throws InterruptedException {
        // interruptLock();
        // fairLock();
        readWriteLock();
    }

    private static void interruptLock() throws InterruptedException {
        Lock lock = new ReentrantLock();
        Runnable run = () -> {
            try {
                System.out.printf("%s try to lock\n", Thread.currentThread().getName());
                lock.lockInterruptibly();

                try {
                    System.out.printf("%s get lock\n", Thread.currentThread().getName());
                    Thread.sleep(1000 * 30);
                } catch (InterruptedException e) {
                    System.out.printf("%s sleep interrupted\n", Thread.currentThread().getName());
                } finally {
                    lock.unlock();
                    System.out.printf("%s unlock\n", Thread.currentThread().getName());
                }
            } catch (InterruptedException e) {
                System.out.printf("%s wait interrupted\n", Thread.currentThread().getName());
            }
        };

        Thread thread1 = new Thread(run);
        Thread thread2 = new Thread(run);

        thread1.start();
        thread2.start();

        Thread.sleep(1000);
        thread1.interrupt();
        Thread.sleep(1000);
        thread2.interrupt();
    }

    private static void fairLock() throws InterruptedException {
        // 默认是公平锁
        // tryLock  是非公平的，即使设置为公平锁，也会插队
        ReentrantLock lock = new ReentrantLock(true);
        Runnable run = () -> {
            System.out.printf("%s begin\n", Thread.currentThread().getName());
            lock.lock();
            try {
                int durationSeconds = (int) (Math.random() * 5);

                System.out.printf("%s running task1 %d seconds\n", Thread.currentThread().getName(), durationSeconds);
                Thread.sleep(durationSeconds * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

            lock.lock();
            try {
                int durationSeconds = (int) (Math.random() * 5);

                System.out.printf("%s running task2 %d seconds\n", Thread.currentThread().getName(), durationSeconds);
                Thread.sleep(durationSeconds * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            System.out.printf("%s end\n", Thread.currentThread().getName());
        };

        Thread[] threads = new Thread[3];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(run);
        }

        for (Thread thread : threads) {
            thread.start();
            Thread.sleep(100);
        }
    }

    private static void readWriteLock() {
        // 非公平锁，写锁可随时插队，读锁仅在等待队列头节点不是想获取写锁的线程时可以插队
        // 公平锁，不允许插队
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

        // 可以降级、不能升级（避免升级）
        Runnable readFunc = () -> {
            readLock.lock();
            try {
                System.out.printf("%s get read lock\n", Thread.currentThread().getName());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.printf("%s release read lock\n", Thread.currentThread().getName());
                readLock.unlock();
            }
        };

        Runnable writeFunc = () -> {
            writeLock.lock();
            try {
                System.out.printf("%s get write lock\n", Thread.currentThread().getName());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.printf("%s release write lock\n", Thread.currentThread().getName());
                writeLock.unlock();
            }
        };

        new Thread(writeFunc, "writer1").start();
        new Thread(readFunc, "reader1").start();
        new Thread(readFunc, "reader2").start();
        new Thread(writeFunc, "writer2").start();
        new Thread(readFunc, "reader3").start();
    }
}
