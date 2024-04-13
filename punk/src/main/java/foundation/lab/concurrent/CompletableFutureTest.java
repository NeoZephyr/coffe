package foundation.lab.concurrent;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class CompletableFutureTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        List<CompletableFuture<Result>> futures = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            long time = 1000 + i * 1000;
            int finalI = i;
            CompletableFuture<Result> future = CompletableFuture.supplyAsync(() -> {
//                try {
//                    Thread.sleep(time);
//                    if (finalI % 2 == 0) {
//                        throw new RuntimeException("fuck");
//                    }
//
//                    log.info("task {} complete", finalI);
//                    return new Result(true, String.format("hello %d", finalI));
//                } catch (Throwable ex) {
//                    log.info("task {} complete", finalI);
//                    // return new Result(false, String.format("hello %d", finalI));
//                    throw ex;
//                }
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.info("task {} complete", finalI);
                if (finalI % 2 == 0) {
                    throw new RuntimeException("fuck");
                }

                return new Result(true, String.format("hello %d", finalI));
            });
            futures.add(future);
        }

        CompletableFuture<Void> f = CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0]));

//        try {
//            f.join();
//        } catch (Exception e) {
//            log.info("------ fukc");
//        }

        while (!f.isDone()) {
            Thread.sleep(1000);
            log.info("=== processing");
        }

        log.info("all task ok");

        for (CompletableFuture<?> future : futures) {
            Result o = (Result) future.get();
            log.info("{}", o);
        }
    }

    @Data
    static class Result {
        boolean success;
        String data;

        public Result(boolean success, String data) {
            this.success = success;
            this.data = data;
        }
    }
}
