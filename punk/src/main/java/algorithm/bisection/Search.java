package algorithm.bisection;

import sun.misc.Unsafe;

public class Search {

    public static void main(String[] args) {
        String s = new StringBuilder("jav").append("va").toString();
        System.out.println(s.intern() == s);
        Unsafe unsafe = Unsafe.getUnsafe();
        long l = unsafe.allocateMemory(100);
    }

    /**
     * 有序数组找 >= target 的最左位置
     */
    public int findLeft(int[] arr, int target) {
        int l = 0;
        int r = arr.length - 1;
        int ans = -1;

        while (l <= r) {
            int m = l + (r - l) / 2;

            if (arr[m] >= target) {
                ans = m; // 记录一个答案
                r = m - 1; // m 右边的都大于等于 target
            } else {
                l = m + 1;
            }
        }

        return ans;
    }

    // 有序数组找 <= target 的最右位置

    /**
     * 寻找一个峰值
     * 假设数组之外的为无穷小
     * 数组中相邻两个数不相等
     */
    public int findTop(int[] arr) {
        int n = arr.length - 1;

        if (n == 1) {
            return 0;
        }

        // 验证 0 位置是不是峰值
        if (arr[0] > arr[1]) {
            return 0;
        }

        // 验证 n - 1 位置是不是峰值
        if (arr[n - 1] > arr[n - 2]) {
            return n - 1;
        }

        // 0 到 1 位置是递增
        // n - 2 到 n - 1 是递减
        // 1 到 n - 2 一定有峰值

        int l = 1;
        int r = n - 2;
        int ans = -1;

        while (l <= r) {
            int m = (l + r) / 2;

            if (arr[m] < arr[m - 1]) { // m - 1 到 m 是递减
                r = m - 1;
            } else if (arr[m] < arr[m + 1]) { // m 到 m + 1 是递增
                l = m + 1;
            } else {
                ans = m;
                break;
            }
        }

        return ans;
    }
}
