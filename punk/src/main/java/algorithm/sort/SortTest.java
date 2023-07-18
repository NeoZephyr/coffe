package algorithm.sort;

import algorithm.structure.utils.ArrayUtils;

import java.util.Arrays;

public class SortTest {
    public static void main(String[] args) {
        boolean result;

        for (int i = 0; i < 1000; ++i) {
            int[] data1 = ArrayUtils.genIntArray(1000, 0, 20);
            int[] data2 = Arrays.copyOf(data1, 1000);
            // new BubbleSort().sort(data1);
            // new BubbleSort().borderSort(data1);

            // new InsertSort().sort(data1);
            // new InsertSort().shellSort(data1);

            // new SelectSort().sort(data1);

            // new MergeSort().sort(data1);

            // new QuickSort().sort(data1);

            new BucketSort().sort(data1);

            Arrays.sort(data2);
            result = ArrayUtils.compare(data1, data2);

            if (!result) {
                System.out.println("ERROR");
                System.out.printf("data1: %s\n", Arrays.toString(data1));
                System.out.printf("data2: %s\n", Arrays.toString(data2));
                break;
            }
        }

        System.out.println("Complete");
    }
}
