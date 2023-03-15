package net.grpc;

import java.time.Duration;

public interface RetryStrategy {
    Duration getRetryDelay(RetryFactor retryFactor);
}
