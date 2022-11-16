package rate;

import com.google.common.util.concurrent.RateLimiter;

public class Limit {

    public static void main(String[] args) {
        RateLimiter limiter = RateLimiter.create(5);
        double v = limiter.acquire(30);
        limiter.tryAcquire(10);
        System.out.println("v: " + v + ", t: " + System.currentTimeMillis());
        v = limiter.acquire(30);
        System.out.println("v: " + v + ", t: " + System.currentTimeMillis());
        // 1667886924128
        // 1667886918120
    }
}
