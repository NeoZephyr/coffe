package algorithm.leetcode.recursion;

public class Powx {

    public double myPow(double x, int n) {
        if (x == 0) {
            return 0;
        }

        if (n == 0) {
            return 1;
        }

        if (n == 1) {
            return x;
        }

        if (n == -1) {
            return 1 / x;
        }

        if ((n & 1) == 1) {
            return x * myPow(x * x, n >> 1);
        } else {
            return myPow(x * x, n >> 1);
        }
    }

    /**
     * 2^13 = 2^(1101) = 2^(1000) * 2^(100) * 2^(1)
     *
     */
    public double myPow1(double x, int n) {
        double ans = 1.0;
        long m = n;

        // 注意 -2147483648，不能用 -n 转为正数
        if (n < 0) {
            m = -m;
        }

        while (m != 0) {
            if ((m & 1) == 1) {
                ans *= x;
            }

            // 前进一位，就提升一次权重
            x = x * x;
            m = m >> 1;
        }

        if (n >= 0) {
            return ans;
        } else {
            return 1.0 / ans;
        }
    }

    public static void main(String[] args) {
        int n = -2147483648;
        long m = -n;

        // 还是负数
        System.out.println(m);
    }
}
