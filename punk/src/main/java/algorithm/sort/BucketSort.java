package algorithm.sort;

public class BucketSort {
    /**
     * 操作步骤：
     * 将要排序的数据分到几个有序的桶里，每个桶里的数据再单独进行排序。桶内排完序之后，再把每个桶里的数据按照顺序依次取出
     *
     * 时间复杂度：
     * 如果要排序的数据有 n 个，均匀地划分到 m 个桶内，每个桶里就有 k = n / m 个元素。每个桶内部使用快速排序，时间复杂度为 O(k * logk)。
     * m 个桶排序的时间复杂度就是 O(m * k * logk)，也就是 O(n * log(n / m))。当桶的个数 m 接近数据个数 n 时，log(n / m) 就是一个
     * 非常小的常量，这个时候桶排序的时间复杂度接近 O(n)
     *
     * 桶排序条件：
     * 1. 要排序的数据需要很容易就能划分成 m 个桶
     * 2. 桶与桶之间有着天然的大小顺序。这样每个桶内的数据都排序完之后，桶与桶之间的数据不需要再进行排序
     * 3. 数据在各个桶之间的分布是比较均匀的。如果数据经过桶的划分之后，数据都被划分到一个桶里，那就退化为 O(n * logn) 的排序算法
     *
     * 桶排序比较适合用在外部排序中
     */

    /**
     *
     * 计数排序是桶排序的一种特殊情况。当要排序的 n 个数据，所处的范围并不大的时候，比如最大值是 k，就可以把数据划分成 k 个桶。
     * 每个桶内的数据值都是相同的，省掉了桶内排序的时间
     */
    public void sort(int[] data) {
        if (data == null || data.length == 0 || data.length == 1) {
            return;
        }

        // 2, 5, 3, 0, 2, 3, 0, 3

        int max = data[0];

        for (int i = 0; i < data.length; ++i) {
            if (data[i] > max) {
                max = data[i];
            }
        }

        int[] numberToCount = new int[max + 1];

        // 0 -> 2, 1 -> 0, 2 -> 2, 3 -> 3, 4 -> 0, 5 -> 1
        for (int i = 0; i < data.length; ++i) {
            numberToCount[data[i]]++;
        }

        // 0 -> 2, 1 -> 2, 2 -> 4, 3 -> 7, 4 -> 7, 5 -> 8
        // numberToCount[i] 的值表示小于等于 i 的元素的个数
        for (int i = 1; i < numberToCount.length; ++i) {
            numberToCount[i] = numberToCount[i] + numberToCount[i - 1];
        }

        int[] tmpData = new int[data.length];

        // 找出每一个元素应该存储的位置
        for (int i = data.length - 1; i >= 0; --i) {
            int count = numberToCount[data[i]];
            --numberToCount[data[i]];
            tmpData[count - 1] = data[i];
        }

        for (int i = 0; i < data.length; ++i) {
            data[i] = tmpData[i];
        }
    }
}
