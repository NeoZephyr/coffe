package algorithm.sort;

import algorithm.structure.utils.ArrayUtils;
import algorithm.structure.utils.RandomUtils;

import java.util.Arrays;

public class KthLookupTest {
    public static void main(String[] args) {
        boolean result;

        for (int i = 0; i < 10; ++i) {
            int[] data1 = ArrayUtils.genIntArray(1000, 0, 10000);
            int[] data2 = Arrays.copyOf(data1, 1000);

            int k = RandomUtils.randomInt(1, 1000 + 1);
            Arrays.sort(data2);

            int kth1 = new KthLookup().lookup(data1, k);
            int kth2 = data2[k - 1];

            result = (kth1 == kth2);

            if (!result) {
                System.out.println("ERROR");
                System.out.printf("data1: %s\n", Arrays.toString(data1));
                System.out.printf("data2: %s\n", Arrays.toString(data2));
                System.out.printf("k: %d, kth1: %d, kth2: %d\n", k, kth1, kth2);
                break;
            }
        }

        System.out.println("Complete");
    }
}
