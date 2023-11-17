package algorithm.leetcode.array;

public class MinimumSizeSubArraySum {

    // 滑动窗口
    public int minSubArrayLen1(int s, int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        int i = 0, j = -1;

        int sum = 0;
        int len = nums.length + 1;

        while (i < nums.length) {
            if (sum < s && j + 1 < nums.length) {
                sum += nums[++j];
            } else {
                sum -= nums[i++];
            }

            if (sum >= s) {
                len = Math.min(len, j - i + 1);
            }

        }

        if (len == nums.length + 1) {
            return 0;
        }

        return len;
    }

    public int minSubArrayLen2(int s, int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        int i = 0, j = -1;

        int sum = 0;
        int len = nums.length + 1;

        while (j + 1 < nums.length) {

            while (j + 1 < nums.length && sum < s) {
                sum += nums[++j];
            }

            if (sum >= s) {
                len = Math.min(len, j - i + 1);
            }

            while (i < nums.length && sum >= s) {
                sum -= nums[i++];
                if(sum >= s)
                    len = Math.min(len, j - i + 1);
            }
        }

        if (len == nums.length + 1) {
            return 0;
        }

        return len;
    }
}
