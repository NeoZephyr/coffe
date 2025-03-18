package algorithm.leetcode.dp;

/**
 * https://leetcode.cn/problems/partition-equal-subset-sum/
 */
public class PartitionEqual {

    public static void main(String[] args) {
        PartitionEqual partitionEqual = new PartitionEqual();
        System.out.println(partitionEqual.canPartition1(new int[]{1,5,11,5}));
        System.out.println(partitionEqual.canPartition1(new int[]{1,2,3,5}));
    }

    public boolean canPartition(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return false;
        }

        int sum = 0;

        for (int num : nums) {
            sum += num;
        }

        if (sum % 2 == 1) {
            return false;
        }

        return help(nums, 0, sum / 2);
    }

    public boolean canPartition1(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return false;
        }

        int sum = 0;

        for (int num : nums) {
            sum += num;
        }

        if (sum % 2 == 1) {
            return false;
        }

        int len = nums.length;
        int target = sum / 2;

        // dp[i][j] 表示 nums 数组的前 i 个数能否凑够数值 j
        // 还可以进行空间优化
        boolean[][] dp = new boolean[len + 1][target + 1];
        dp[0][0] = true;

        for (int i = 1; i <= len; i++) {
            for (int j = 1; j <= target; j++) {
                if (j >= nums[i - 1]) {
                    dp[i][j] = dp[i - 1][j] || dp[i - 1][j - nums[i - 1]];
                } else {
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }

        return dp[len][target];
    }

    private boolean help(int[] nums, int pos, int target) {
        if (pos >= nums.length) {
            return false;
        }

        if (target == 0) {
            return true;
        }

        // target < 0 就是 false 也要考虑

        return help(nums, pos + 1, target) || help(nums, pos + 1, target - nums[pos]);
    }
}
