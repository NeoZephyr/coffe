package algorithm.sort;

public class MergeSort {
    /**
     *
     * 内存消耗：
     * 尽管每次合并操作都需要申请额外的内存空间，但在合并完成之后，临时开辟的内存空间就被释放掉了。在任意时刻，CPU 只会有一个函数在执行，
     * 也就只会有一个临时的内存空间在使用。临时内存空间最大也不会超过 n 个数据的大小，所以空间复杂度是 O(n)
     *
     * 稳定性：
     * 稳定的排序算法
     *
     * 时间复杂度：
     * 归并排序的执行效率与要排序的原始数组的有序程度无关，所以其时间复杂度是非常稳定的，不管是最好情况、最坏情况，还是平均情况，
     * 时间复杂度都是 O(n * logn)
     *
     * @param data
     */
    public void sort(int[] data) {
        if (data == null || data.length == 0 || data.length == 1) {
            return;
        }

        mergeSort(data, 0, data.length - 1);
    }

    private void mergeSort(int[] data, int l, int r) {
        if (l >= r) {
            return;
        }

        int mid = (l + r) / 2;
        mergeSort(data, l, mid);
        mergeSort(data, mid + 1, r);
        merge(data, l, mid, r);
    }

    private void merge(int[] data, int begin, int mid, int end) {
        int len = (end - begin + 1);
        int[] tmp = new int[len];

        int p1 = begin;
        int p2 = mid + 1;
        int p = 0;

        while (p1 <= mid && p2 <= end) {
            if (data[p1] <= data[p2]) {
                tmp[p++] = data[p1++];
            } else {
                tmp[p++] = data[p2++];
            }
        }

        while (p1 <= mid) {
            tmp[p++] = data[p1++];
        }

        while (p2 <= end) {
            tmp[p++] = data[p2++];
        }

        int i = 0;
        int j = begin;

        while (i < len) {
            data[j++] = tmp[i++];
        }
    }
}
