package net.grpc;

import java.time.Duration;
import java.util.Random;
import java.util.function.Function;

public class BackoffRetryStrategy implements RetryStrategy {

    private Function<RetryFactor, Boolean> allowRetryFunc;
    private int maxRetryTimes;
    private int maxRetryInterval;
    private int backoffBase;
    private Random random;

    public BackoffRetryStrategy(Function<RetryFactor, Boolean> allowRetryFunc, int maxRetryTimes, int maxRetryInterval, int backoffBase) {
        this.allowRetryFunc = allowRetryFunc;
        this.maxRetryTimes = maxRetryTimes;
        this.maxRetryInterval = maxRetryInterval;
        this.backoffBase = backoffBase;
        this.random = new Random();
    }

    @Override
    public Duration getRetryDelay(RetryFactor retryFactor) {
        if (!allowRetryFunc.apply(retryFactor)) {
            return Duration.ZERO;
        }

        int retryTimes = retryFactor.getRetryTimes();

        if (retryTimes >= maxRetryTimes) {
            return Duration.ZERO;
        }

        long backoffTime = Math.min(
                maxRetryInterval,
                backoffBase * (1L << Math.min(retryTimes, 16)) + random.nextInt(backoffBase));
        return Duration.ofMillis(backoffTime);
    }
}