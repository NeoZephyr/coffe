package algorithm.leetcode.array;

public class RotateArray {

    public void rotate1(int[] nums, int k) {
        if (nums == null || nums.length < 2) {
            return;
        }

        k = k % nums.length;

        if (k == 0) {
            return;
        }

        swap(nums, 0, nums.length - 1);
        swap(nums, 0, k - 1);
        swap(nums, k, nums.length - 1);
    }

    private void swap(int[] nums, int start, int end) {
        while (start < end) {
            int tmp = nums[start];
            nums[start] = nums[end];
            nums[end] = tmp;

            ++start;
            --end;
        }
    }

    public void rotate2(int[] nums, int k) {
        if (nums == null || nums.length < 2 || k < 1) {
            return;
        }

        k = k % nums.length;
        int count = 0;

        for (int i = 0; i < nums.length; ++i) {
            int curPos = i;
            int curVal = nums[curPos];

            do {
                int nextPos = (curPos + k) % nums.length;
                int nextVal = nums[nextPos];
                nums[nextPos] = curVal;
                curPos = nextPos;
                curVal = nextVal;
                ++count;
            } while (curPos != i);

            if (count == nums.length) {
                break;
            }
        }
    }
}
