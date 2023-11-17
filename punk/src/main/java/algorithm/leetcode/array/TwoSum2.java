package algorithm.leetcode.array;

public class TwoSum2 {

    // Input array is sorted
    // 有序数组，指针对撞的方式
    public int[] twoSum(int[] numbers, int target) {
        assert numbers != null && numbers.length >= 2;

        int i = 0, j = numbers.length - 1;

        while (i < j) {
            int sum = numbers[i] + numbers[j];

            if (sum < target) {
                ++i;
            } else if (sum > target) {
                --j;
            } else {
                return new int[]{i + 1, j + 1};
            }
        }

        return null;
    }
}
