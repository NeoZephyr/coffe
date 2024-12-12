package algorithm.leetcode.bisection;

public class SearchInRotatedSortedArray {

    public static void main(String[] args) {
        System.out.println(new SearchInRotatedSortedArray().search(new int[]{4,5,6,7,0,1,2}, 0));
    }

    // 跟 nums[0] 或者 nums[n - 1] 对比，可以看出数据段的分布
    public int search(int[] nums, int target) {
        int low = 0;
        int high = nums.length - 1;

        if (nums.length == 0) {
            return -1;
        }

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (target == nums[mid]) {
                return mid;
            }

            if (nums[low] <= nums[mid]) {
                if (target >= nums[low] && target < nums[mid]) {
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            } else {
                if (target > nums[mid] && target <= nums[high]) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
        }

        return -1;
    }
}
