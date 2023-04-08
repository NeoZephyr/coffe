package lab.concurrent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionThread {
    public static void main(String[] args) throws InterruptedException {
        Thread.setDefaultUncaughtExceptionHandler(new ThreadExceptionHandler());
        Runnable run = () -> {
            throw new RuntimeException("crashed");
        };
        Thread thread = new Thread(run);
        thread.start();
        thread.join();
    }

    static class ThreadExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            log.warn("catch exception: ", e);
        }
    }
}
