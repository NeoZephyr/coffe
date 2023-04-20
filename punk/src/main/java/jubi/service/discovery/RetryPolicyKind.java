package jubi.service.discovery;

public enum RetryPolicyKind {

    /** A retry policy that retries only once */
    ONE_TIME,

    /** A retry policy that retries a max number of times */
    N_TIME,

    /**
     * A retry policy that retries a set number of times with increasing sleep time between
     *  retries
     */
    EXPONENTIAL_BACKOFF,

    /**
     * A retry policy that retries a set number of times with an increasing (up to a maximum
     *  bound) sleep time between retries
     */
    BOUNDED_EXPONENTIAL_BACKOFF,

    /** A retry policy that retries until a given amount of time elapses */
    UNTIL_ELAPSED;

//    public RetryPolicy valueOf(String policy) {
//        if (ONE_TIME.name().equals(policy)) {
//            return ONE_TIME;
//        } else if (N_TIME.name().equals(policy)) {
//            return N_TIME;
//        } else if (EXPONENTIAL_BACKOFF.name().equals(policy)) {
//            return EXPONENTIAL_BACKOFF;
//        } else if (BOUNDED_EXPONENTIAL_BACKOFF.name().equals(policy)) {
//            return BOUNDED_EXPONENTIAL_BACKOFF;
//        } else if (UNTIL_ELAPSED.name().equals(policy)) {
//            return UNTIL_ELAPSED;
//        }
//    }
}
