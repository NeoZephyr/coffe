package algorithm.leetcode.dp;

import java.util.Arrays;

public class HouseRobber {

    public static void main(String[] args) {
        HouseRobber houseRobber = new HouseRobber();
        System.out.println(houseRobber.rob3(new int[]{5,2,7,9,3,10}));
        System.out.println(houseRobber.rob3(new int[]{1,2,3,1}));
        System.out.println(houseRobber.rob3(new int[]{2,7,9,3,1}));
    }

    public int rob(int[] nums) {
        int[] dp = new int[nums.length];

        // 邻居的值
        int neighbor = 0;

        // 邻居之前的最大
        int lastMax = 0;
        int max = 0;

        for (int i = 0; i < nums.length; i++) {
            dp[i] = lastMax + nums[i];
            lastMax = Math.max(lastMax, neighbor);
            neighbor = dp[i];
            max = Math.max(dp[i], max);
        }

        return max;
    }

    public int rob1(int[] nums) {
        int[] mem = new int[nums.length];
        Arrays.fill(mem, -1);
        return dfs(nums, nums.length - 1, mem);
    }

    // dfs(i) 表示从 nums[0] 到 nums[i] 最多能偷多少
    private int dfs(int[] nums, int pos, int[] mem) {
        if (pos < 0) {
            return 0;
        }

        if (mem[pos] != -1) {
            return mem[pos];
        }

        int res = Math.max(
                dfs(nums, pos - 1, mem),
                dfs(nums, pos - 2, mem) + nums[pos]);
        mem[pos] = res;
        return res;
    }

    // dp[i] 从 0 到 i 偷到的最大值，结果解释 dp[n - 1]
    // dp[i] = dp[i - 1] + nums[i]
    // 问题：第 i - 1 天，偷了吗？
    // dp[i][0, 1] 二维状态表示是否偷了
    // dp[i][0] = max(dp[i - 1][0], dp[i - 1][1])  第 i 个房子不偷，第 i - 1 个可偷可不偷
    // dp[i][1] = max(dp[i - 1][0], 0) + nums[i]   第 i 个房子偷，第 i - 1 个不能偷

    // 状态优化
    // dp[i] 从 0 到 i 偷到的最大值，且 nums[i] 必偷的最大值，结果是 max(dp[1...n-1])
    // dp[i] = max(dp[i - 1], dp[i - 2] + nums[i], dp[i - 3] + nums[i], dp[i - 4] + nums[i])
    // 可以证明只需要到 dp[i - 2] 就够了

    // dp[i] 表示从 i 个房子里面偷到的最大值
    // 偷 i 个房子的方法：
    // 1. 如果不偷最后一个房子，变成从 i - 1 个房子偷，是 dp[i - 1]
    // 2. 如果偷最后一个房子，那么第 i - 1 个房子是不能偷的，变成从前 i - 2 个房子偷，是 dp[i - 2] + num

    // 动态规划有两种计算顺序：
    // 一种是自顶向下的、使用备忘录的递归方法
    // 一种是自底向上的、使用 dp 数组的循环方法

    public int rob2(int[] nums) {
        return dfs1(nums, 0, false);
    }

    private int dfs1(int[] nums, int pos, boolean stolen) {
        // 没房屋可供偷窃，只能空手而归
        if (pos >= nums.length) {
            return 0;
        }

        if (stolen) { // 上一个已经偷过了
            return dfs1(nums, pos + 1, false);
        } else { // 上一个没有偷，当前的房子可以偷或者不偷
            return Math.max(
                    dfs1(nums, pos + 1, false),
                    dfs1(nums, pos + 1, true) + nums[pos]);
        }
    }

    public int rob3(int[] nums) {
        // 两个数组分别记录偷与不偷第 i 个房屋的情况下第 i 个房屋及之后能偷窃到的最大值
        int[] stolen = new int[nums.length];
        int[] notStolen = new int[nums.length];
        Arrays.fill(stolen, - 1);
        Arrays.fill(notStolen, - 1);

        return dfs2(nums, 0, false, stolen, notStolen);
    }

    /**
     *                                      -> 偷房屋 3 (重复计算一次)
     *         偷房屋 1 ------> 不偷房屋 2 -->
     *                                      -> 不偷房屋 3
     * root ->
     *                    ---> 偷房屋 2 ------> 不偷房屋 3
     *         不偷房屋 1  ->
     *                                      -> 偷房屋 3 (重复计算两次次)
     *                    ---> 不偷房屋 2 -->
     *                                      -> 不偷房屋 3
     */
    private int dfs2(int[] nums, int pos, boolean stolen, int[] st, int[] nst) {
        // 没房屋可供偷窃，只能空手而归
        if (pos >= nums.length) {
            return 0;
        }

        if (stolen) { // 上一个已经偷过了
            if (nst[pos] != -1) {
                return nst[pos];
            }

            nst[pos] = dfs2(nums, pos + 1, false, st, nst);
            return nst[pos];
        } else { // 上一个没有偷，当前的房子可以偷或者不偷
            if (nst[pos] == -1) {
                nst[pos] = dfs2(nums, pos + 1, false, st, nst);
            }

            if (st[pos] == -1) {
                st[pos] = dfs2(nums, pos + 1, true, st, nst);
            }

            return Math.max(nst[pos], st[pos] + nums[pos]);
        }
    }
}
