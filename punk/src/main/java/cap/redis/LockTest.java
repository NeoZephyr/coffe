package cap.redis;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LockTest {

    public static void main(String[] args) throws InterruptedException {
        Config config = new Config();
        config.setUseScriptCache(true);
        String address = "redis://127.0.0.1:6379";

        config.useSingleServer()
                .setAddress(address)
                .setSubscriptionConnectionMinimumIdleSize(10)
                .setSubscriptionsPerConnection(50);
        RedissonClient client = Redisson.create(config);
        RLock lock = client.getReadWriteLock("redis").readLock();
        lock.lock(20, TimeUnit.SECONDS);
    }

    public static void benchmark(RedissonClient client) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            int idx = i;
            service.submit(() -> {
                log.info("prepare to lock: {}", idx);
                RLock locker;

                if (idx % 2 == 0) {
                    locker = wlock(client);
                } else {
                    locker = rlock(client);
                }

                log.info("already lock: {}", idx);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                locker.unlock();
                log.info("unlock: {}", idx);
            });
        }

        service.awaitTermination(100, TimeUnit.SECONDS);
    }

    public static RLock wlock(RedissonClient client) {
        RLock lock = client.getReadWriteLock("ddd").writeLock();
        lock.lock(20, TimeUnit.SECONDS);
        return lock;
    }

    public static RLock rlock(RedissonClient client) {
        RLock lock = client.getReadWriteLock("ddd").readLock();
        lock.lock(20, TimeUnit.SECONDS);
        return lock;
    }

}
