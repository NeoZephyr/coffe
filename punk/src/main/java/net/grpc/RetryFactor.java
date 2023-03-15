package net.grpc;

public class RetryFactor {
    private String method;
    private String status;
    private int retryTimes;

    private RetryFactor() {}

    public String getMethod() {
        return method;
    }

    public String getStatus() {
        return status;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String method;
        private String status;
        private int retryTimes;

        // private RetryFactor retryFactor;

        private Builder() {
            // ignore
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder retryTimes(int retryTimes) {
            this.retryTimes = retryTimes;
            return this;
        }

        public RetryFactor build() {
            RetryFactor retryFactor = new RetryFactor();
            retryFactor.method = this.method;
            retryFactor.retryTimes = this.retryTimes;
            retryFactor.status = this.status;
            return retryFactor;
        }
    }
}
