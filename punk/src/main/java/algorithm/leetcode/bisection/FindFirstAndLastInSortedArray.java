package algorithm.leetcode.bisection;

import java.util.Arrays;

public class FindFirstAndLastInSortedArray {

    public static void main(String[] args) {
        FindFirstAndLastInSortedArray instance = new FindFirstAndLastInSortedArray();
        int[] ans = instance.searchRange(new int[]{5, 7, 7, 8, 8, 10}, 8);
        System.out.println(Arrays.toString(ans));
        ans = instance.searchRange(new int[]{5, 7, 7, 8, 8, 10}, 6);
        System.out.println(Arrays.toString(ans));
    }

    public int[] searchRange(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return new int[]{-1, -1};
        }

        int[] ans = new int[2];
        int left = findFirst(nums, target);

        if (left == -1) {
            return new int[]{-1, -1};
        }

        int right = findLast(nums, target);

        ans[0] = left;
        ans[1] = right;
        return ans;
    }

    private int findFirst(int[] nums, int target) {
        int low = 0;
        int high = nums.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (nums[mid] > target) {
                high = mid - 1; // mid 左边有可能是 target 第一次出现的位置
            } else if (nums[mid] < target) {
                low = mid + 1; // 下一轮搜索区间是 [mid + 1, right]
            } else {
                // 往左找
                // mid 及 mid 左边有可能是 target 第一次出现的位置
                high = mid - 1;
            }
        }

        if (high < nums.length - 1 && nums[high + 1] == target) {
            return high + 1;
        }

        return -1;
    }

    private int findLast(int[] nums, int target) {
        int low = 0;
        int high = nums.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;

            if (nums[mid] > target) {
                high = mid - 1;
            } else if (nums[mid] < target) {
                low = mid + 1;
            } else {
                low = mid + 1; // 往右找
            }
        }

        if (low > 0 && nums[low - 1] == target) {
            return low - 1;
        }

        return -1;
    }

    private int findFirst1(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] < target) {
                // 一定不是解
                // 下一轮搜索区间是 [mid + 1, right]
                left = mid + 1;
            } else {
                // nums[mid] == target，mid 及 mid 左边有可能是 target 第一次出现的位置
                // nums[mid] > target, mid 左边有可能是 target 第一次出现的位置
                // 下一轮搜索区间是 [left, mid]
                right = mid;
            }
        }

        // while(left < right) 只表示退出循环以后有 left == right 成立，不表示搜索区间为左闭右开区间

        // 退出循环以后不能确定 nums[left] 是否等于 target，因此需要再判断一次
        if (nums[left] == target) {
            return left;
        }
        return -1;
    }

    public int[] searchRange1(int[] nums, int target) {
        int start = lowerBound(nums, target);

        if (start == nums.length || nums[start] != target) {
            return new int[]{-1, -1}; // nums 中没有 target
        }
        // 如果 start 存在，那么 end 必定存在
        int end = lowerBound(nums, target + 1) - 1;
        return new int[]{start, end};
    }

    // lowerBound 返回最小的满足 nums[i] >= target 的下标 i
    // 如果数组为空，或者所有数都 < target，则返回 nums.length
    private int lowerBound(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1; // 闭区间 [left, right]
        while (left <= right) { // 区间不为空
            // 循环不变量：
            // nums[left - 1] < target
            // nums[right + 1] >= target
            int mid = left + (right - left) / 2;

            if (nums[mid] >= target) {
                right = mid - 1; // 范围缩小到 [left, mid-1]
            } else {
                left = mid + 1; // 范围缩小到 [mid+1, right]
            }
        }
        // 循环结束后 left = right + 1
        // 此时 nums[left - 1] < target 而 nums[left] = nums[right + 1] >= target
        // 所以 left 就是第一个 >= target 的元素下标
        return left;
    }

    // 有序数组中找 >=num 的最左位置
    // 有序数组中找 >=num 的最右位置，没有意义，因为直接判断 nums[n - 1] 就可以了
    private int findLeft(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        int ans = -1;

        while (left <= right) {
            int mid = left + ((right - left) >> 1);

            if (nums[mid] >= target) {
                ans = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return ans;
    }

    // 有序数组中找 <=num 的最右位置
    private int findRight(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        int ans = -1;

        while (left <= right) {
            int mid = left + ((right - left) >> 1);

            if (nums[mid] <= target) {
                ans = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return ans;
    }
}
