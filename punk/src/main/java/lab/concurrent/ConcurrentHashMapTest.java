package lab.concurrent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConcurrentHashMapTest {

    public static void main(String[] args) throws InterruptedException {
        int threadCount = 10;
        // int loopCount = 10000000;
        int loopCount = 100;
        int itemCount = 10;

        Map<String, Long> result = testSyncPut(threadCount, loopCount, itemCount);
        long sum = result.values().stream().mapToLong(l -> l).reduce(0, Long::sum);
        System.out.println(sum);

        result = testComputeIfAbsent(threadCount, loopCount, itemCount);
        sum = result.values().stream().mapToLong(l -> l).reduce(0, Long::sum);
        System.out.println(sum);
    }

    private static Map<String, Long> testSyncPut(int threadCount, int loopCount, int itemCount) throws InterruptedException {
        ConcurrentHashMap<String, Long> freq = new ConcurrentHashMap<>(itemCount);
        ForkJoinPool pool = new ForkJoinPool(threadCount);
        pool.execute(() -> IntStream.rangeClosed(1, loopCount).parallel().forEach(item -> {
            String key = "item" + ThreadLocalRandom.current().nextInt(itemCount);

            System.out.println(Thread.currentThread().getName());

            synchronized (freq) {
                if (freq.containsKey(key)) {
                    freq.put(key, freq.get(key) + 1);
                } else {
                    freq.put(key, 1L);
                }
            }
        }));
        Thread.sleep(10000);
        pool.shutdown();
        pool.awaitTermination(3, TimeUnit.SECONDS);
        return freq;
    }

    private static Map<String, Long> testComputeIfAbsent(int threadCount, int loopCount, int itemCount) throws InterruptedException {
        ConcurrentHashMap<String, LongAdder> freq = new ConcurrentHashMap<>(itemCount);
        ForkJoinPool pool = new ForkJoinPool(threadCount);
        pool.execute(() -> IntStream.rangeClosed(1, loopCount).parallel().forEach(item -> {
            String key = "item" + ThreadLocalRandom.current().nextInt(itemCount);
            freq.computeIfAbsent(key, k -> new LongAdder()).increment();
        }));
        pool.shutdown();
        pool.awaitTermination(3, TimeUnit.SECONDS);
        return freq.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().longValue()
        ));
    }
}
