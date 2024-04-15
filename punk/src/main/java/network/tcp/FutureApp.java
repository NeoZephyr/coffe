package network.tcp;

import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

@Slf4j
public class FutureApp {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        testPromise();
    }

    private static void testPromise() throws ExecutionException, InterruptedException {
        DefaultEventLoop eventExecutors = new DefaultEventLoop();
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventExecutors);

        promise.addListener(future -> {
            log.info("listen: {}", future.getNow());
        });

        eventExecutors.execute(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // log.info("set success: {}",10);
            // promise.setSuccess(10);

            RuntimeException ex = new RuntimeException("WTF");
            log.info("set failure", ex);
            promise.setFailure(ex);
        });

        log.info("start...");
        log.info("getNow: {}",promise.getNow());
        // log.info("get: {}",promise.get());

        promise.await(); // 与 sync 和 get 区别在于，不会抛异常
        log.info("await result: {}", (promise.isSuccess() ? promise.getNow() : promise.cause()).toString());
    }
}
