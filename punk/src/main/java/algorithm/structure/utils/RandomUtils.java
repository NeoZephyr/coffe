package algorithm.structure.utils;

import java.util.Random;

public class RandomUtils {
    private static final Random random = new Random();

    static {
        random.setSeed(System.currentTimeMillis());
    }

    public static int randomInt(int start, int end) {
        return random.nextInt(end - start) + start;
    }
}
