package algorithm.leetcode.greedy;

import java.util.HashMap;
import java.util.Map;

public class JumpGame {

    public static void main(String[] args) {
        System.out.println(new JumpGame().canJump(new int[]{3,2,1,0,4}));
    }

    // 反向
    public boolean canJump(int[] nums) {
        int target = nums.length - 1;
        int pos = nums.length - 2;
        boolean ans = true;

        while (pos >= 0) {
            if (nums[pos] + pos >= target) {
                target = pos;
                ans = true;
                pos--;
            } else {
                ans = false;
                pos--;
            }
        }

        return ans;
    }

    // 正向
    // 维护最远可以到达的位置
    public boolean canJump1(int[] nums) {
        int max = 0;

        for (int i = 0; i < nums.length; i++) {

            if (i > max) {
                return false;
            }

            max = Math.max(max, i + nums[i]);
        }

        return true;
    }

    public boolean canJump2(int[] nums) {
        if (nums == null || nums.length == 0) {
            return true;
        }
        Map<Integer, Boolean> cache = new HashMap<>();
        return dfs(0, nums, cache);
    }

    private boolean dfs(int pos, int[] nums, Map<Integer, Boolean> cache) {
        if (pos >= nums.length - 1) {
            return true;
        }

        if (cache.containsKey(pos)) {
            return cache.get(pos);
        }

        for (int i = 1; i <= nums[pos]; ++i) {
            if (dfs(i + pos, nums, cache)) {
                cache.put((i + pos), Boolean.TRUE);
                return true;
            }
        }
        cache.put(pos, Boolean.FALSE);
        return false;
    }
}
