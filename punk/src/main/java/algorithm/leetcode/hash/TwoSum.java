package algorithm.leetcode.hash;

import java.util.HashMap;
import java.util.Map;

public class TwoSum {

    public int[] twoSum(int[] nums, int target) {
        if (nums == null || nums.length < 2) {
            return null;
        }

        Map<Integer, Integer> map = new HashMap<>();
        int[] result = new int[2];

        for (int i = 0; i < nums.length; i++) {
            Integer idx = map.get(target - nums[i]);

            if (idx != null && idx < i) {
                result[0] = idx;
                result[1] = i;
                return result;
            }

            map.put(nums[i], i);
        }

        return null;
    }
}
