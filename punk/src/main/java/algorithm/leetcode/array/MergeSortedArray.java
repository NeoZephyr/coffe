package algorithm.leetcode.array;

public class MergeSortedArray {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        if (n == 0) {
            return;
        }

        int k = m + n - 1;
        int i = m - 1;
        int j = n - 1;

        while (i >= 0 && j >= 0) {
            if (nums1[i] > nums2[j]) {
                nums1[k--] = nums1[i--];
            } else {
                nums1[k--] = nums2[j--];
            }
        }

        while (i >= 0) {
            nums1[k--] = nums1[i--];
        }

        while (j >= 0) {
            nums1[k--] = nums2[j--];
        }
    }

    public void merge1(int[] nums1, int m, int[] nums2, int n) {
        int[] copyNums1 = new int[m];

        for (int i = 0; i < m; ++i) {
            copyNums1[i] = nums1[i];
        }

        int i = 0;
        int j = 0;
        int pos = 0;

        while (i < m && j < n) {
            if (copyNums1[i] < nums2[j]) {
                nums1[pos++] = copyNums1[i++];
            } else {
                nums1[pos++] = nums2[j++];
            }
        }

        while (i < m) {
            nums1[pos++] = copyNums1[i++];
        }

        while (j < n) {
            nums1[pos++] = nums2[j++];
        }
    }

    public void merge2(int[] nums1, int m, int[] nums2, int n) {
        int i = m - 1;
        int j = n - 1;
        int pos = m + n - 1;

        while (i >= 0 && j >= 0) {
            if (nums1[i] > nums2[j]) {
                nums1[pos--] = nums1[i--];
            } else {
                nums1[pos--] = nums2[j--];
            }
        }

        while (j >= 0) {
            nums1[pos--] = nums2[j--];
        }
    }
}
