package algorithm.leetcode.greedy;

public class JumpGame2 {

    public static void main(String[] args) {
        System.out.println(new JumpGame2().jump(new int[]{2,3,1,1,4}));
        System.out.println(new JumpGame2().jump(new int[]{2,3,0,1,4}));;
    }
    // [0]

    public int jump(int[] nums) {
        int times = 0;
        int maxRight = -1;
        int curRight = 0;

        for (int i = 0; i < nums.length; i++) {
            if (curRight >= nums.length - 1) {
                break;
            }

            maxRight = Math.max(maxRight, nums[i] + i);

            // 到达已建造的桥的右端点
            if (i >= curRight) {
                curRight = maxRight;
                times++;
            }
        }

        return times;
    }
}
