package algorithm.sort;

import java.util.Arrays;

public class SelectSort {
    /**
     * 操作步骤：
     * 类似插入排序，也分已排序区间和未排序区间。但是选择排序每次会从未排序区间中找到最小的元素，将其放到已排序区间的末尾
     *
     * 内存消耗：
     * 空间复杂度为 O(1)，是一种原地排序算法
     *
     * 稳定性：
     * 选择排序每次都要找剩余未排序元素中的最小值，并和前面的元素交换位置，这样破坏了稳定性
     *
     * 时间复杂度：
     * 最好情况时间复杂度、最坏情况和平均情况时间复杂度都为 O(n * n)
     *
     * @param data
     */
    public void sort(int[] data) {
        if (data == null || data.length < 2) {
            return;
        }

        for (int i = 0; i < data.length - 1; ++i) {
            int minIdx = i;

            for (int j = i + 1; j < data.length; ++j) {
                if (data[j] < data[minIdx]) {
                    minIdx = j;
                }
            }

            if (minIdx != i) {
                int tmp = data[minIdx];
                data[minIdx] = data[i];
                data[i] = tmp;
            }
        }
    }

    public static void main(String[] args) {
        SelectSort selectSort = new SelectSort();
        int[] a = new int[]{3, 9, 1, 20, 8};
        System.out.println(Arrays.toString(a));
        selectSort.sort(a);
        System.out.println(Arrays.toString(a));
    }
}