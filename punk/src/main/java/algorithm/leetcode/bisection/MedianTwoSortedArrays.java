package algorithm.leetcode.bisection;

public class MedianTwoSortedArrays {

    public static void main(String[] args) {
        double ans = new MedianTwoSortedArrays().findMedianSortedArrays(new int[]{1, 3}, new int[]{2});
        System.out.println(ans);
    }

    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int len1 = nums1.length;
        int len2 = nums2.length;
        int total = len1 + len2;

        if ((total & 1) == 0) {
            int left = kth(nums1, nums2, 0, len1 - 1, 0, len2 - 1, total / 2);
            int right = kth(nums1, nums2, 0, len1 - 1, 0, len2 - 1, total / 2 + 1);
            return (left + right) / 2.0;
        } else {
            return kth(nums1, nums2, 0, len1 - 1, 0, len2 - 1, total / 2 + 1);
        }
    }

    // 两个有序数组的时候，用画分界线的方式来找中位数。这条分割线的特点是：
    // 1. 当数组的总长度为偶数的时候，分割线左右的数字个数总和相等；当数组的总长度为奇数的时候，分割线左数字个数比右边仅仅多 1
    // 2. 分割线左边的所有元素都小于等于（不大于）分割线右边的所有元素

    // 如果这条分割线可以找到，那么中位数就可以确定下来，同样得分奇偶性：
    // 1. 当数组的总长度为偶数的时候，中位数就是分割线左边的最大值与分割线右边的最小值的平均数
    // 2. 当数组的总长度为奇数的时候，中位数就是分割线左边的最大值。因此，在数组长度是奇数的时候，中位数就是分割线左边的所有数的最大值

    // len(nums1) = L1, len(nums2) = L2
    // 当数组的总长度为偶数的时候，左边一共有 (L1 + L2) / 2 = (L1 + L2 + 1) / 2
    // 当数组的总长度为奇数的时候，左边一共有 (L1 + L2) / 2 + 1 = (L1 + L2 + 1) / 2

    // 分割线左边的元素总数是固定的，只要能确定 1 个数组上元素的位置，另一个位置自然就可以确定下来
    // 在其中一个数组找到 i 个元素，则另一个数组的元素个数就一定是 (L1 + L2 + 1) / 2 - i

    public double findMedianSortedArrays1(int[] nums1, int[] nums2) {
        // 始终保证 nums1 为较短的数组，nums1 过长可能导致 j 为负数而越界
        if (nums1.length > nums2.length) {
            int[] tmp = nums1;
            nums1 = nums2;
            nums2 = tmp;
        }

        int m = nums1.length;
        int n = nums2.length;

        int total = (m + n + 1) / 2;

        // 使得 nums1[i - 1] <= nums2[j] && nums2[j - 1] <= nums1[i]
        int left = 0;
        int right = m;

        // 判定交叉的关系：
        // 1. 第 1 个数组分割线左边的第 1 个数小于等于第 2 个数组分割线右边的第 1 的数
        // 2. 第 2 个数组分割线左边的第 1 个数小于等于第 1 个数组分割线右边的第 1 的数
        //
        // nums1[i-1] <= nums2[j] && nums2[j-1] <= nums1[i]
        // 由于该条件等价于在 [0, m] 中找到最大的 i 使得 nums1[i-1] <= nums2[j]
        // 假设已经找到了满足条件的最大 i，使得 nums1[i-1] <= nums2[j]，此时必有 nums[i] > nums2[j]，进而有 nums[i] > nums2[j-1]
        while (left < right) {
            // 对整数来说，向上取整等于：(被除数 + (除数 - 1)) / 除数
            // 避免 left + 1 = right 时可能无法继续缩小区间而陷入死循环
            int i = left + (right - left + 1) / 2;
            int j = total - i;

            if (nums1[i - 1] > nums2[j]) {
                right = i - 1;
            } else {
                left = i;
            }
        }

        int i = left;
        int j = total - i;

        int leftMax1 = i == 0 ? Integer.MIN_VALUE : nums1[i - 1]; // 分割线左边没有元素
        int rightMin1 = i == m ? Integer.MAX_VALUE : nums1[i]; // 分割线右边没有元素
        int leftMax2 = j == 0 ? Integer.MIN_VALUE : nums2[j - 1];
        int rightMin2 = j == n ? Integer.MAX_VALUE : nums2[j];

        if (((m + n) % 2) == 1) {
            return Math.max(leftMax1, leftMax2);
        } else {
            return (double) ((Math.max(leftMax1, leftMax2) + Math.min(rightMin1, rightMin2))) / 2;
        }
    }

    private int kth(int[] nums1, int[] nums2, int start1, int end1, int start2, int end2, int k) {
        int len1 = end1 - start1 + 1;
        int len2 = end2 - start2 + 1;

        if (len1 > len2) {
            return kth(nums2, nums1, start2, end2, start1, end1, k);
        }

        if (len1 == 0) {
            return nums2[start2 + k - 1];
        }

        if (k == 1) {
            return Math.min(nums1[start1], nums2[start2]);
        }

        int idx1 = start1 + Math.min(k / 2, len1) - 1;
        int idx2 = start2 + Math.min(k / 2, len2) - 1;

        // nums1: [start1, start1 + k / 2 - 1]
        // nums2: [start2, start2 + k / 2 - 1]
        // a = nums1[start1 + k / 2 - 1], b = nums2[start2 + k / 2 - 1]
        // 如果 a > b
        // b 前面有 k / 2 - 1 个数，a 前面也有 k / 2 - 1 个数
        // 那么比 b 要小的数，最多有 (k / 2  + k / 2) - 2 个，b 最多是第 (k / 2 + k / 2) - 1 个数，最多是第 k - 1 个数
        // 因此 num2 里面的 start2 到 start2 + k / 2 - 1 的数，可以放心排除掉，因为第 k 个数肯定不在这里面

        if (nums1[idx1] >= nums2[idx2]) {
            return kth(nums1, nums2, start1, end1, idx2 + 1, end2, k - (idx2 - start2 + 1));
        } else {
            return kth(nums1, nums2, idx1 + 1, end1, start2, end2, k - (idx1 - start1 + 1));
        }
    }
}
