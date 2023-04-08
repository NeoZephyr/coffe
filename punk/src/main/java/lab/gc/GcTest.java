package lab.gc;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class GcTest {
    public static void main(String[] args) {
        // printMemoryInfo();
        genGarbage();
    }

    // -Xmx10m
    private static void printMemoryInfo() {
        System.out.println(Runtime.getRuntime().totalMemory() / (1024 * 1024));
        System.out.println(Runtime.getRuntime().freeMemory() / (1024 * 1024));
        System.out.println(Runtime.getRuntime().maxMemory() / (1024 * 1024));
    }

    private static Random random = new Random();

    private static void genGarbage() {
        long startMillis = System.currentTimeMillis();
        long timeoutMillis = TimeUnit.SECONDS.toMillis(1);
        long endMillis = startMillis + timeoutMillis;
        LongAdder counter = new LongAdder();
        System.out.println("开始...");

        int cacheSize = 2000;
        Object[] cachedGarbage = new Object[cacheSize];

        while (System.currentTimeMillis() < endMillis) {
            Object garbage = generateGarbage(1024 * 100);
            counter.increment();
            int randomIndex = random.nextInt(2 * cacheSize);
            if (randomIndex < cacheSize) {
                cachedGarbage[randomIndex] = garbage;
            }
        }
        System.out.println("结束！共生成对象次数:" + counter.longValue());
    }

    private static Object generateGarbage(int max) {
        int dataSize = random.nextInt(max);
        int type = dataSize % 4;
        Object result = null;
        switch (type) {
            case 0:
                result = new int[dataSize];
                break;
            case 1:
                result = new byte[dataSize];
                break;
            case 2:
                result = new double[dataSize];
                break;
            default:
                StringBuilder builder = new StringBuilder();
                String randomString = "randomString‐Anything";
                while (builder.length() < dataSize) {
                    builder.append(randomString);
                    builder.append(dataSize);
                }
                result = builder.toString();
                break;
        }
        return result;
    }
}
