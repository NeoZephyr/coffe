package algorithm.leetcode.dp;

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

    public static void main(String[] args) {
        ClimbingStairs climbingStairs = new ClimbingStairs();
        System.out.println(climbingStairs.climbStairs3(3));
        System.out.println(climbingStairs.climbStairs3(1));
    }

    // 记忆化搜索
    public int climbStairs3(int n) {
        int[] mem = new int[n + 1];
        dfs(mem, n);
        return mem[n];
    }

    private int dfs(int[] mem, int i) {
        if (i <= 1) {
            mem[i] = 1;
            return 1;
        }

        if (mem[i] != 0) {
            return mem[i];
        }

        mem[i] = dfs(mem, i - 1) + dfs(mem, i - 2);
        return mem[i];
    }
}
