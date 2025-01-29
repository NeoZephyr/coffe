package algorithm.leetcode.bisection;

/**
 * https://leetcode.cn/problems/find-peak-element/
 */
public class FindPeakElement {

    public static void main(String[] args) {
        FindPeakElement instance = new FindPeakElement();
        System.out.println(instance.findPeakElement(new int[]{1,2,3,1}));
    }

    // 假设 nums[-1] = nums[n] = -∞

    public int findPeakElement(int[] nums) {
        if (nums.length == 1) {
            return 0;
        }

        if (nums[0] > nums[1]) {
            return 0;
        }

        int n = nums.length;

        if (nums[n - 1] > nums[n - 2]) {
            return n - 1;
        }

        // 左侧上升趋势，右侧下降趋势，中间一定有峰值
        int left = 1;
        int right = n - 2;
        int ans = -1;

        while (left <= right) {
            int mid = left + ((right - left) >> 1);

            if ((nums[mid] > nums[mid - 1]) && (nums[mid] > nums[mid + 1])) {
                ans = mid;
                break;
            }

            if (nums[mid] < nums[mid - 1]) {
                right = mid - 1; // 右侧下降趋势，中间一定有峰值
            } else {
                // nums[mid] < nums[mid + 1]
                // 左侧上升趋势，中间一定有峰值
                left = mid + 1;
            }
        }

        return ans;
    }

    public int findPeakElement1(int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > nums[mid + 1]) {
                right = mid; // 左侧存在峰值
            } else {
                left = mid + 1; // 右侧存在峰值
            }
        }
        return left;
    }
}
