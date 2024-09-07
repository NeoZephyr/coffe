package algorithm.leetcode.recursion;

import java.util.Arrays;

public class MajorityElement {

    // method1: 将数组 nums 排序，数组中点的元素 一定为众数
    public int majorityElement(int[] nums) {
        Arrays.sort(nums);
        return nums[nums.length >> 1];
    }

    // method2: 票数正负抵消
    public int majorityElement1(int[] nums) {
        int cand = nums[0];
        int vote = 1;

        for (int i = 1; i < nums.length; i++) {
            if (vote == 0) {
                cand = nums[i];
                vote = 1;
                continue;
            }

            if (cand == nums[i]) {
                vote++;
                continue;
            }

            vote--;
        }

        return cand;
    }
}
