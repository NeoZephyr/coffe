package algorithm.leetcode.bisection;

public class FindMinimumRotatedSortedArray {

    public static void main(String[] args) {
        System.out.println(new FindMinimumRotatedSortedArray().findMin(new int[]{3, 4, 5, 1, 2}));
        System.out.println(new FindMinimumRotatedSortedArray().findMin(new int[]{4, 5, 6, 7, 0, 1, 2}));
        System.out.println(new FindMinimumRotatedSortedArray().findMin(new int[]{11, 13, 15, 17}));
    }

    public int findMin(int[] nums) {
        int low = 0;
        int high = nums.length - 1;

        while (true) {

            if (nums[low] <= nums[high]) {
                return nums[low];
            }

            int mid = low + (high - low) / 2;

            if (nums[low] <= nums[mid]) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
    }

    public int findMin1(int[] nums) {
        int low = 0;
        int high = nums.length - 1;
        while (low < high) {
            int pivot = low + (high - low) / 2;

            if (nums[pivot] < nums[high]) { // 这说明 nums[pivot] 是最小值右侧的元素，因此可以忽略二分查找区间的右半部分
                high = pivot;
            } else { // 说明 nums[pivot] 是最小值左侧的元素，因此可以忽略二分查找区间的左半部分
                low = pivot + 1;
            }
        }
        return nums[low];
    }
}
