package algorithm.leetcode.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreeSum {

    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();

        if (nums == null || nums.length < 3) {
            return result;
        }

        Arrays.sort(nums);

        int i = 0;
        while (i < nums.length) {
            if (nums[i] > 0) {
                break;
            }

            int left = i + 1;
            int right = nums.length - 1;

            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];

                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));

                    while (++left < right && nums[left] == nums[left - 1]);
                    while (left < --right && nums[right] == nums[right + 1]);
                } else if (sum < 0) {
                    ++left;
                } else {
                    --right;
                }
            }

            while (++i < nums.length && nums[i] == nums[i - 1]);
        }

        return result;
    }
}
