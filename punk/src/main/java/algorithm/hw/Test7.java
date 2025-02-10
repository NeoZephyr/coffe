package algorithm.hw;

/**
 * Example 1:
 * Input: nums = [1,5,11,5]
 * Output: true
 * Explanation: The array can be partitioned as [1, 5, 5] and [11].
 * Example 2:
 * Input: nums = [1,2,3,5]
 * Output: false
 * Explanation: The array cannot be partitioned into equal sum subsets
 */
public class Test7 {

    public static void main(String[] args) {}

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

        // 前 i 个数 凑够 j
        // dp[i][j]
        // dp[i]
        // dp[i - nums[j]]

        return help(nums, 0, sum / 2);
    }

    private boolean help(int[] nums, int pos, int target) {
        if (pos >= nums.length) {
            return false;
        }

        if (target == 0) {
            return true;
        }

        return help(nums, pos + 1, target) || help(nums, pos + 1, target - nums[pos]);
    }
}
