package algorithm.acm.math;

public class Lcm {

    public static void main(String[] args) {
        System.out.println(new Lcm().lcm(10, 14));
    }

    public int lcm(int a, int b) {
        int hcf = (a > b ? gcd(a, b) : gcd(b, a));
        return hcf * (a / hcf) * (b / hcf);
    }

    private int gcd(int m, int s) {
        while (s != 0) {
            // 求 m, s 的最大公约数
            // 等价于求 s, m % s 的最大公约数
            int tmp = m % s;
            m = s;
            s = tmp;
        }

        return m;
    }
}
