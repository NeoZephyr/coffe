package foundation.lab.concurrent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionThread {
    public static void main(String[] args) throws InterruptedException {
        interrupt();
    }

    private static void exception() throws InterruptedException {
        Thread.setDefaultUncaughtExceptionHandler(new ThreadExceptionHandler());
        Runnable run = () -> {
            throw new RuntimeException("crashed");
        };
        Thread thread = new Thread(run);
        thread.start();
        thread.join();
    }

    private static void interrupt() throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // 当一个线程抛出 InterruptedException 异常时,它的中断状态会被自动清除
                        log.info("===== interrupt {} =====", Thread.interrupted());

                        // 重新设置中断状态
                        Thread.currentThread().interrupt();

                        log.info("===== interrupt {} =====", Thread.interrupted());
                        throw new RuntimeException(e);
                    }
                    log.info("===== work =====");
                }
            }
        });
        t1.start();
        Thread.sleep(1000);
        t1.interrupt();
        log.info("===== interrupt {} =====", t1.isInterrupted());
        Thread.sleep(1000);
        log.info("===== interrupt {} =====", t1.isInterrupted());
    }

    static class ThreadExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            log.warn("catch exception: ", e);
        }
    }
}
