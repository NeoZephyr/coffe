package algorithm.leetcode.dp;

public class MaximumProductSubarray {

    public static void main(String[] args) {
        MaximumProductSubarray instance = new MaximumProductSubarray();
        System.out.println(instance.maxProduct(new int[]{2,3,-2,4}));
        System.out.println(instance.maxProduct(new int[]{-2,0,-1}));
        System.out.println(instance.maxProduct(new int[]{0,-3,-2,0,-1,-10}));
        System.out.println(instance.maxProduct(new int[]{0}));
        System.out.println(instance.maxProduct(new int[]{-2}));
        System.out.println(instance.maxProduct(new int[]{2,-5,-2,-4,3}));
        System.out.println(instance.maxProduct(new int[]{2,-5,-2,-4,3, -2}));
    }

    public int maxProduct(int[] nums) {
        int[] dp1 = new int[nums.length];
        int[] dp2 = new int[nums.length];
        dp1[0] = nums[0];
        dp2[0] = nums[0];
        int max = nums[0];

        for (int i = 1; i < nums.length; i++) {
            int p1 = nums[i] * dp1[i - 1];
            int p2 = nums[i] * dp2[i - 1];
            dp1[i] = Math.max(Math.max(p1, p2), nums[i]);
            dp2[i] = Math.min(Math.min(p1, p2), nums[i]);

            max = Math.max(dp1[i], max);
        }

        return max;
    }
}
