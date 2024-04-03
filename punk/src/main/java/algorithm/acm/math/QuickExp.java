package algorithm.acm.math;

public class QuickExp {

    // 快速幂运算，二分加速
    public int exp(int a, int n) {
        int ans = 1;

        while (n > 0) {
            if (n % 2 == 1) {
                ans *= a;
            }

            // 底数平方
            a = a * a;

            // 指数减半
            n = n / 2;
        }

        return ans;
    }
}
