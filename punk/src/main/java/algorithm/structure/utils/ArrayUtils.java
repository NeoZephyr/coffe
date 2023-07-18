package algorithm.structure.utils;

import java.util.Arrays;
import java.util.HashSet;

public class ArrayUtils {
    public static int[] genIntArray(int size, int start, int end) {
        int[] arr = new int[size];

        for (int i = 0; i < size; ++i) {
            arr[i] = RandomUtils.randomInt(start, end);
        }

        return arr;
    }

    public static int[] genDistinctIntArray(int size, int start, int end) {
        if (size > (end - start + 1)) {
            throw new RuntimeException("range error");
        }
        int[] arr = new int[size];
        HashSet<Integer> set = new HashSet<>();

        for (int i = 0; i < size;) {
            int cand = RandomUtils.randomInt(start, end);

            if (!set.contains(cand)) {
                set.add(cand);
                arr[i++] = cand;
            }
        }

        return arr;
    }

    public static boolean compare(int[] arr1, int[] arr2) {
        if (arr1 == null && arr2 == null) {
            return true;
        }

        if (arr1 == null || arr2 == null) {
            return false;
        }

        if (arr1.length != arr2.length) {
            return false;
        }

        int n = arr1.length;

        for (int i = 0; i < n; ++i) {
            if (arr1[i] != arr2[i]) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; ++i) {
            Thread.sleep(1);
            int[] arr = genIntArray(10, 0, 100);
            System.out.println(Arrays.toString(arr));
        }
    }
}
