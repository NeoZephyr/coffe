package algorithm.leetcode.array;

public class ClimbingStairs {
    public int climbStairs1(int n) {
        if (n == 1) {
            return 1;
        }

        if (n == 2) {
            return 2;
        }

        return climbStairs1(n - 1) + climbStairs1(n -2);
    }

    public int climbStairs2(int n) {
        int ways1 = 1;
        int ways2 = 2;

        if (n == 1) {
            return 1;
        }

        if (n == 2) {
            return 2;
        }

        for (int i = 3; i <= n; ++i) {
            int tmp = ways1 + ways2;
            ways1 = ways2;
            ways2 = tmp;
        }

        return ways2;
    }
}
