package algorithm.leetcode.hash;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TwoSum {

    // 由于要返回的是数组下标，所以排序方法不行（位置变了）
    // 哈希表方法
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
