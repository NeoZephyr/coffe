package algorithm.leetcode.bisection;

public class Sqrtx {

    public static void main(String[] args) {
        System.out.println("aaa");
        System.out.println(new Sqrtx().mySqrt(6));
        System.out.println(new Sqrtx().mySqrt(8));
        System.out.println(new Sqrtx().mySqrt(9));
        System.out.println(new Sqrtx().mySqrt(10));
    }

    public int mySqrt(int x) {
        if (x == 0 || x == 1) {
            return x;
        }

        // [1, x)
        int low = 1;
        int high = Math.min(46341, x);

        while (low < high) {
            int mid = low + (high - low) / 2;

            if (mid * mid > x) {
                // 1. [low, high = mid)       大于等于 high 的部分 -> 大于根号 x
                high = mid;
            } else {
                // 2. [low = mid + 1, high)   小于 low 的部分 -> 小于等于根号 x
                low = mid + 1;
            }

            // 1, 3
            System.out.println("low: " + low + ", high: " + high);
        }

        return low - 1;
    }

    int s;

    // 牛顿迭代法
    public int mySqrt1(int x) {
        s = x;

        if (x == 0) {
            return 0;
        }

        return ((int) (sqrts(x)));
    }

    public double sqrts(double x) {
        double res = (x + s / x) / 2;

        if (res == x) {
            return x;
        } else {
            return sqrts(res);
        }
    }
}
