package algorithm.sort;

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
        if (data == null || data.length == 0 || data.length == 1) {
            return;
        }

        for (int i = 1; i < data.length; ++i) {
            int candPos = i - 1;

            for (int j = i; j < data.length; ++j) {
                if (data[j] < data[candPos]) {
                    candPos = j;
                }
            }

            if (candPos != i - 1) {
                int tmp = data[candPos];
                data[candPos] = data[i - 1];
                data[i - 1] = tmp;
            }
        }
    }
}