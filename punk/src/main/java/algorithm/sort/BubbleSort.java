package algorithm.sort;

public class BubbleSort {

    /**
     *
     * 操作步骤：
     * 1. 每次冒泡操作都会对相邻的两个元素进行比较，看是否满足大小关系要求。如果不满足就让它俩互换
     * 2. 一次冒泡会让至少一个元素移动到它应该在的位置，重复 n - 1 次，就完成了 n 个数据的排序工作
     * 3. 当某次冒泡操作已经没有数据交换时，说明已经达到完全有序，不用再继续执行后续的冒泡操作
     *
     * 内存消耗：
     * 冒泡的过程只涉及相邻数据的交换操作，只需要常量级的临时空间，所以它的空间复杂度为 O(1)，是一个原地排序算法
     *
     * 稳定性：
     * 在冒泡排序中，只有交换才可以改变两个元素的前后顺序。为了保证冒泡排序算法的稳定性，当有相邻的两个元素大小相等的时候，
     * 不做交换，相同大小的数据在排序前后不会改变顺序，所以冒泡排序是稳定的排序算法
     *
     * 时间复杂度：
     * 最好情况下，要排序的数据已经是有序的了，只需要进行一次冒泡操作，就可以结束了，时间复杂度是 O(n)
     * 最坏情况下，要排序的数据刚好是倒序排列的，需要进行 n 次冒泡操作，时间复杂度为 O(n * n)
     *
     *
     * @param data
     */
    public void sort(int[] data) {
        if (data == null || data.length == 0 || data.length == 1) {
            return;
        }

        for (int i = data.length - 1; i > 0; --i) {
            boolean swap = false;

            for (int j = 0; j < i; ++j) {
                if (data[j] > data[j + 1]) {
                    int tmp = data[j + 1];
                    data[j + 1] = data[j];
                    data[j] = tmp;
                    swap = true;
                }
            }

            if (!swap) {
                return;
            }
        }
    }

    public void borderSort(int[] data) {
        if (data == null || data.length == 0 || data.length == 1) {
            return;
        }

        int lastSwap = 0;
        int sortBorder = data.length - 1;

        for (int i = 1; i < data.length; ++i) {
            boolean swap = false;

            for (int j = 0; j < sortBorder; ++j) {
                if (data[j] > data[j + 1]) {
                    int tmp = data[j + 1];
                    data[j + 1] = data[j];
                    data[j] = tmp;
                    swap = true;
                    lastSwap = j;
                }
            }

            if (!swap) {
                break;
            }

            // 记录最后一次发生交换的位置，下一次的循环中不考虑再次之后的元素
            sortBorder = lastSwap;
        }
    }
}
