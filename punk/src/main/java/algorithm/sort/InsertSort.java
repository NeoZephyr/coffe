package algorithm.sort;

public class InsertSort {
    /**
     * 操作步骤：
     * 1. 将数组中的数据分为两个区间，已排序区间和未排序区间
     * 2. 初始已排序区间只有一个元素，就是数组的第一个元素
     * 3. 取未排序区间中的元素，在已排序区间中找到合适的插入位置将其插入，并保证已排序区间数据一直有序
     * 4. 重复这个过程，直到未排序区间中元素为空
     *
     *
     * 内存消耗：
     * 插入排序算法的运行并不需要额外的存储空间，空间复杂度是 O(1)，是一个原地排序算法
     *
     * 稳定性：
     * 在插入排序中，对于值相同的元素，可以选择将后面出现的元素，插入到前面出现元素的后面，以保持原有的前后顺序不变，
     * 所以插入排序是稳定的排序算法
     *
     * 时间复杂度：
     * 最好情况下，数据已经是有序的，并不需要搬移任何数据。如果从尾到头在有序数据组里面查找插入位置，
     * 每次只需要比较一个数据就能确定插入的位置，时间复杂度为 O(n)
     *
     * 最坏情况下，数据是倒序的，每次插入都相当于在数组的第一个位置插入新的数据，需要移动大量的数据，时间复杂度为 O(n * n)
     *
     * @param data
     */
    public void sort(int[] data) {
        if (data == null || data.length == 0 || data.length == 1) {
            return;
        }

        for (int i = 1; i < data.length; ++i) {
            int cur = data[i];
            int j = i - 1;

            for (; j >= 0; --j) {
                if (data[j] > cur) {
                    data[j + 1] = data[j];
                } else {
                    break;
                }
            }

            data[j + 1] = cur;
        }
    }

    /**
     * 如果一个很大的元素在最前面，如果还是普通插入排序，会有很多次的挪动
     * shell 插入排序可以让大的元素快点挪动到后面，减少移动次数
     */
    public void shellSort(int[] data) {
        if (data == null || data.length == 0 || data.length == 1) {
            return;
        }

        int gap = data.length / 2;

        while (gap >= 1) {
            for (int i = gap; i < data.length; i += gap) {
                int cur = data[i];
                int j = i - gap;

                for (; j >= 0; j -= gap) {
                    if (data[j] > cur) {
                        data[j + gap] = data[j];
                    } else {
                        break;
                    }
                }

                data[j + gap] = cur;
            }

            gap /= 2;
        }
    }
}
