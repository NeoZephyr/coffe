package algorithm.leetcode.recursion;

public class ClimbingStairs {

    public int climbStairs(int n) {
        int f1 = 1;
        int f2 = 2;

        if (n == 1) {
            return f1;
        }

        if (n == 2) {
            return f2;
        }

        for (int i = 3; i <= n; i++) {
            int tmp = f1 + f2;
            f1 = f2;
            f2 = tmp;
        }

        return f2;
    }
}
