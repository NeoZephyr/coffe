package algorithm.sort;

import static org.apache.commons.lang3.ArrayUtils.swap;

public class HeapSort {

    // 元素添加与堆调整同时进行
    public static <T extends Comparable<T>> void heapSort1(T[] nums) {
        MaxHeap<T> maxHeap = new MaxHeap<>(nums.length + 1);
        for (T num : nums) {
            maxHeap.insert(num);
        }

        for (int i = nums.length - 1; i >= 0; --i) {
            nums[i] = maxHeap.popup();
        }
    }

    // 先添加全部元素，后整体调整
    public static <T extends Comparable<T>> void heapSort2(T[] nums) {
        MaxHeap<T> maxHeap = new MaxHeap<>(nums);

        for (int i = nums.length - 1; i >= 0; --i) {
            nums[i] = maxHeap.popup();
        }
    }

    // 原地排序
    public static <T extends  Comparable<T>> void heapSort3(T[] nums) {
        int n = nums.length;

        // 从最后一个非叶子节点开始
        for (int i = (n - 1) / 2; i >= 0; --i) {
            siftDown(nums, n, i);
        }

        for (int i = n - 1; i > 0; --i) {
            swap(nums, 0, i);
            siftDown(nums, i, 0);
        }
    }

    private static <T extends Comparable<T>> void siftDown(T[] nums, int n, int k) {
        while (2 * k + 1 < n) {
            int cand = 2 * k + 1;
            if (cand + 1 < n && nums[cand + 1].compareTo(nums[cand]) > 0) {
                cand = cand + 1;
            }

            if (nums[k].compareTo(nums[cand]) > 0) {
                break;
            }
            // TODO 优化：交换改为覆盖
            T tmp = nums[cand];

            nums[cand] = nums[k];
            nums[k] = tmp;
            k = cand;
        }
    }
}

class MaxHeap<T extends Comparable<T>> {
    private T[] data;
    private int count;

    MaxHeap(int capacity) {
        data = (T[]) new Comparable[capacity + 1];
        count = 0;
    }

    MaxHeap(T[] nums) {
        data = (T[]) new Comparable[nums.length + 1];
        for (int i = 0; i < nums.length; ++i) {
            data[i + 1] = nums[i];
        }

        count = nums.length;

        for (int i = nums.length / 2; i >= 1; --i) {
            shiftDown(i);
        }
    }

    public int getSize() {
        return count;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    private void shiftUp(int k) {

        while (k > 1 && data[k].compareTo(data[k / 2]) > 0) {
            T tmp = data[k];
            data[k] = data[k / 2];
            data[k / 2] = tmp;
            k /= 2;
        }
    }

    private void shiftDown(int k) {
        while (2 * k <= count) {
            int cand = 2 * k;
            if (cand + 1 <= count && data[cand + 1].compareTo(data[cand]) > 0) {
                cand = cand + 1;
            }

            if (data[k].compareTo(data[cand]) > 0) {
                break;
            }
            // TODO 优化：交换改为覆盖
            T tmp = data[cand];

            data[cand] = data[k];
            data[k] = tmp;
            k = cand;
        }
    }

    public void insert(T item) {
        data[++count] = item;
        shiftUp(count);
    }

    public T popup() {
        T item = data[count];
        T tmp = data[1];
        data[1] = item;
        data[count] = tmp;

        --count;
        shiftDown(1);
        return tmp;
    }
}
