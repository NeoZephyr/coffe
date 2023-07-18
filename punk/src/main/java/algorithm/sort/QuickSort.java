package algorithm.sort;

public class QuickSort {
    /**
     * 时间复杂度：
     * 在分区极其均衡的情况下，时间复杂度为 O(n * logn)；在分区极其不均衡的情况下，时间复杂度为 O(n * n)
     *
     * 稳定性：
     * 不稳定的排序算法
     *
     * 内存消耗：
     * 原地排序算法
     *
     * @param data
     */
    public void sort(int[] data) {
        if (data == null || data.length == 0 || data.length == 1) {
            return;
        }

        quickSort(data, 0, data.length - 1);
    }

    private void quickSort(int[] data, int begin, int end) {
        if (begin >= end) {
            return;
        }

        int pivot = partition(data, begin, end);
        quickSort(data, begin, pivot - 1);
        quickSort(data, pivot + 1, end);
    }

    /**
     * 类似选择排序，分成两部分已处理区间和未处理区间。小于 i 的区间是小于 pivotValue 的区间，小于 j 的区间是已处理区间
     *
     * @return
     */
    private int partition(int[] data, int begin, int end) {
        int pivotValue = data[end];
        int i = begin;
        int j = begin;

        while (j < end) {
            if (data[j] < pivotValue) {
                int tmp = data[i];
                data[i] = data[j];
                data[j] = tmp;

                ++i;
            }
            ++j;
        }

        data[end] = data[i];
        data[i] = pivotValue;

        return i;
    }
}
