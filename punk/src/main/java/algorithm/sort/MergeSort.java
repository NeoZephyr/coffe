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

        // 优化：当元素较少时可以转而使用插入排序
//        if (r - l <= 15) {
//            insertSort(data, l, r);
//            return;
//        }

        int mid = (l + r) / 2;
        mergeSort(data, l, mid);
        mergeSort(data, mid + 1, r);

        // 优化：已经有序，不再进行排序操作
        if (data[mid] < (data[mid + 1]))
            return;

        merge(data, l, mid, r);
    }

    private void merge(int[] data, int begin, int mid, int end) {
        int len = (end - begin + 1);

        // 辅助数组可以一次性开辟好
        // 比如：new int[max];
        int[] tmp = new int[len];

        // Arrays.copyOfRange

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

    /**
     * https://leetcode.cn/problems/sort-an-array/
     */
    public int[] sortArray(int[] nums) {
        if (nums.length <= 1) {
            return nums;
        }

        sort(nums, 0, nums.length - 1);
        return nums;
    }

    public void sort(int[] nums, int left, int right) {
        if (left == right) {
            return;
        }

        // 注意 >> 的优先级，非常低
        int m = left + ((right - left) >> 1);
        sort(nums, left, m);
        sort(nums, m + 1, right);
        merge(nums, left, m, right);
    }

    /**
     * 非递归
     */
    public int[] sortArray1(int[] nums) {
        for (int step = 1; step < nums.length; step <<= 2) {
            int l = 0;
            int m = l + step - 1;

            while (l < nums.length) {
                // 右半部分缺失，就不需要合并排序
                if (m + 1 >= nums.length) {
                    break;
                }

                // 有可能右半部分不足 step 的个数
                int r = Math.min(nums.length - 1, l + step * 2 - 1);
                merge(nums, l, m, r);
                l = r + 1;
                m = l + step - 1;
            }
        }

        return nums;
    }

    public static void main(String[] args) {
        MergeSort mergeSort = new MergeSort();
        mergeSort.sortArray(new int[]{5,2,3,1});
    }
}