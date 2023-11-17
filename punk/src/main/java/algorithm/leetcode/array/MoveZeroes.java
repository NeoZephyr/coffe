package algorithm.leetcode.array;

public class MoveZeroes {
    public void moveZeroes(int[] nums) {
        assert nums != null && nums.length > 0;

        int pos = 0;
        for (int i = 0; i < nums.length; ++i) {
            if (nums[i] != 0) {
                nums[pos++] = nums[i];
            }
        }

        for (int i = pos; i < nums.length; ++i) {
            nums[i] = 0;
        }
    }

    public void moveZeroes1(int[] nums) {
        int pos = 0;
        int nonZeroPos = 0;

        while (pos < nums.length) {
            if (nums[pos] != 0) {
                nums[nonZeroPos++] = nums[pos];
            }

            pos++;
        }

        while (nonZeroPos < nums.length) {
            nums[nonZeroPos++] = 0;
        }
    }

    public void moveZeroes2(int[] nums) {
        int nonZerosPos = 0;
        int curPos = 0;

        while (curPos < nums.length) {
            if (nums[curPos] != 0) {
                int nonZero = nums[curPos];
                nums[curPos] = nums[nonZerosPos];
                nums[nonZerosPos++] = nonZero;
            }

            curPos++;
        }
    }

    /**
     * 滚雪球
     * https://leetcode.com/problems/move-zeroes/discuss/172432/THE-EASIEST-but-UNUSUAL-snowball-JAVA-solution-BEATS-100-(O(n))-%2B-clear-explanation
     * @param nums
     */
    public void moveZeroes3(int[] nums) {
        int snowBallSize = 0;
        int curPos = 0;

        while (curPos < nums.length) {
            if (nums[curPos] == 0) {
                snowBallSize++;
            } else if (snowBallSize > 0) { // avoid 1st swap
                int tmp = nums[curPos];
                nums[curPos] = nums[curPos - snowBallSize];
                nums[curPos - snowBallSize] = tmp;
            }

            curPos++;
        }
    }
}
