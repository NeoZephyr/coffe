package algorithm.leetcode.bisection;

public class ValidPerfectSquare {

    /**
     * 4 = 1 + 3
     * 9 = 1 + 3 + 5
     * 16 = 1 + 3 + 5 + 7
     *
     */
    public boolean isPerfectSquare(int num) {
        if (num == 1) {
            return true;
        }

        int low = 1;
        int high = Math.min(46341, num);

        while (low < high) {
            int mid = low + (high - low) / 2;
            int target = mid * mid;

            if (target > num) {
                high = mid;
            } else if (target < num) {
                low = mid + 1;
            } else {
                return true;
            }
        }

        return false;
    }
}
