package algorithm.sort;

import java.util.Random;

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

        // 优化：当元素较少时可以转而使用插入排序
//        if (end - begin <= 15) {
//            insertSort(data, begin, end);
//            return;
//        }

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
        // 优化：选择随机位置的元素为轴心
        // swap(nums, start, generate(start, end));

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

    // 解决大量重复元素的情况
    private int partition2(int[] nums, int begin, int end) {
        // 优化：选择随机位置的元素为轴心
        // swap(nums, begin, generate(begin, end));

        int tmp = nums[begin];
        int i = begin + 1;
        int j = end;

        while (true) {

            // 注意边界，不使用 nums[i].compareTo(tmp) <= 0，保证两棵子树的平衡
            // 如果有等于轴点的元素，则平分给两棵子树
            while (i <= end && (nums[i] < tmp)) {
                ++i;
            }

            // 注意边界，不使用 nums[i].compareTo(tmp) >= 0，保证两棵子树的平衡
            while (j >= begin + 1 && (nums[j] > tmp)){
                --j;
            }

            if (i > j) {
                break;
            }

            // swap(nums, i, j);
            ++i;
            --j;
        }

        // j 位置的元素成为最右侧小于 tmp 的元素
        // i 位置的元素成为最左侧大于 tmp 的元素
        // swap(nums, begin, j);
        return j;
    }

    public static void quickSort3Ways(int[] nums) {
        quickSort3Ways(nums, 0, nums.length - 1);
    }

    public static int generate(int start, int end) {
        Random random = new Random();
        return (Math.abs(random.nextInt())) % (end - start + 1) + start;
    }

    private static void quickSort3Ways(int[] nums, int start, int end) {

        // 优化：当元素较少时可以转而使用插入排序
//        if (end - start <= 15) {
//            insertSort(nums, start, end);
//            return;
//        }

        // swap(nums, start, generate(start, end));

        int tmp = nums[start];

        int lt = start; // nums[start + 1...lt] < v
        int gt = end + 1; // nums[gt...end] > v
        int i = start + 1; // nums[lt + 1...i) = v

        while (i < gt) {
            if (nums[i] < tmp) {
                // swap(nums, lt + 1, i);
                ++lt;
                ++i;
            } else if (nums[i] > tmp) {
                // swap(nums, gt - 1, i);
                --gt;
            } else {
                ++i;
            }
        }

        // swap(nums, start, lt);

        quickSort3Ways(nums, start, lt - 1);
        quickSort3Ways(nums, gt, end);
    }
}