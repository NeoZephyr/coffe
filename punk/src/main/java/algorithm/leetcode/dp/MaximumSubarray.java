package algorithm.leetcode.dp;

public class MaximumSubarray {

    public static void main(String[] args) {
        MaximumSubarray instance = new MaximumSubarray();
        System.out.println(instance.maxSubArray(new int[]{-2,1,-3,4,-1,2,1,-5,4}));
        System.out.println(instance.maxSubArray(new int[]{1}));
        System.out.println(instance.maxSubArray(new int[]{5,4,-1,7,8}));
    }

    public int maxSubArray(int[] nums) {
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        int max = dp[0];

        for (int i = 1; i < nums.length; i++) {
            if (dp[i - 1] > 0) {
                dp[i] = dp[i - 1] + nums[i];
            } else {
                dp[i] = nums[i];
            }

            max = Math.max(max, dp[i]);
        }

        return max;
    }
}
